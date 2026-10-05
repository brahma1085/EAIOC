package com.eaioc.controlplane.benchmarking;

import static com.eaioc.controlplane.benchmarking.BaselineTestFixtures.fixtureControlPlaneRequest;
import static com.eaioc.controlplane.benchmarking.BaselineTestFixtures.fixtureLedgerEntry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eaioc.controlplane.accounting.ledger.CostLedgerStore;
import com.eaioc.controlplane.accounting.ledger.LedgerCostReporter;
import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.evaluation.BaselineEvaluationRecord;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * {@code EXE-P0.2.E} (Capability 2 — Baseline Benchmark Harness + Quality Evaluation, Sub-phase E —
 * Security/Governance) tenant-scoping tests, per {@code docs/execution-plan.md} §18.5's E row: "a
 * benchmark run cannot bypass tenant scoping" and "cross-tenant benchmark data leakage is
 * impossible". Contract-level and fixture-based — not a live Path A measurement.
 *
 * <p>The existing {@link BaselineRunnerTest#runBaseline_entryUnderAnotherTenant_throwsWithoutRecord_crossTenantNeverConsumed}
 * covers one cross-tenant miss. These tests add the adversarial cases the E row names: the same
 * {@code request_id} living in two tenants, and a structural check that {@link BaselineRunner} has
 * no path to the ledger other than the tenant-scoped {@link LedgerCostReporter}.
 */
class BaselineTenantScopingTest {

    @Test
    void sameRequestIdInTwoTenants_eachTenantSeesOnlyItsOwnEntry() {
        CostLedgerStore store = new CostLedgerStore();
        store.write(fixtureLedgerEntry("tenant-a", "entry-a", "shared-request", true));
        store.write(fixtureLedgerEntry("tenant-b", "entry-b", "shared-request", true));
        BaselineRunner runner = new BaselineRunner(new LedgerCostReporter(store));

        BaselineEvaluationRecord forA =
            runner.runBaseline(fixtureControlPlaneRequest("tenant-a", "shared-request"));
        BaselineEvaluationRecord forB =
            runner.runBaseline(fixtureControlPlaneRequest("tenant-b", "shared-request"));

        assertThat(forA.tenantId()).isEqualTo("tenant-a");
        assertThat(forA.sourceEntryId()).isEqualTo("entry-a");
        assertThat(forB.tenantId()).isEqualTo("tenant-b");
        assertThat(forB.sourceEntryId()).isEqualTo("entry-b");
    }

    @Test
    void tenantWithoutOwnEntry_cannotConsumeAnotherTenantsVerifiedEntry() {
        CostLedgerStore store = new CostLedgerStore();
        store.write(fixtureLedgerEntry("tenant-a", "entry-a", "request-only-a", true));
        BaselineRunner runner = new BaselineRunner(new LedgerCostReporter(store));
        ControlPlaneRequest fromB = fixtureControlPlaneRequest("tenant-b", "request-only-a");

        assertThatThrownBy(() -> runner.runBaseline(fromB))
            .isInstanceOf(NoBaselineMeasurementException.class);
    }

    @Test
    void otherTenantsDuplicateEntries_doNotBlockAVerifiedEntryInThisTenant() {
        // Count-all-then-verify is scoped per tenant: tenant-a's two matches for the request must not
        // make tenant-b's single verified entry ambiguous, and must not leak into tenant-b's record.
        CostLedgerStore store = new CostLedgerStore();
        store.write(fixtureLedgerEntry("tenant-a", "entry-a-1", "shared-request", true));
        store.write(fixtureLedgerEntry("tenant-a", "entry-a-2", "shared-request", true));
        store.write(fixtureLedgerEntry("tenant-b", "entry-b-1", "shared-request", true));
        BaselineRunner runner = new BaselineRunner(new LedgerCostReporter(store));

        BaselineEvaluationRecord forB =
            runner.runBaseline(fixtureControlPlaneRequest("tenant-b", "shared-request"));

        assertThat(forB.sourceEntryId()).isEqualTo("entry-b-1");
    }

    @Test
    void baselineRunner_hasNoLedgerAccessOtherThanTheTenantScopedReporter() {
        // Structural guard: BaselineRunner's only instance state is the LedgerCostReporter, and its
        // only public method is runBaseline. There is no field or method through which a caller
        // could reach CostLedgerStore's partitions directly.
        Set<String> instanceFieldTypes = Arrays.stream(BaselineRunner.class.getDeclaredFields())
            .filter(f -> !Modifier.isStatic(f.getModifiers()))
            .map(Field::getType)
            .map(Class::getSimpleName)
            .collect(Collectors.toSet());
        assertThat(instanceFieldTypes).containsExactly("LedgerCostReporter");

        Set<String> publicMethods = Arrays.stream(BaselineRunner.class.getMethods())
            .filter(m -> m.getDeclaringClass() == BaselineRunner.class)
            .map(Method::getName)
            .collect(Collectors.toSet());
        assertThat(publicMethods).containsExactly("runBaseline");
    }
}
