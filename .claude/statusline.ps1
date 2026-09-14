$ErrorActionPreference = 'SilentlyContinue'

$inputJson = [Console]::In.ReadToEnd()
$data = $inputJson | ConvertFrom-Json

# --- Line 1: model, context window size, reasoning effort, context remaining ---
$model = $data.model.display_name
if (-not $model) { $model = $data.model.id }

$sizeStr = $null
$cw = $data.context_window.context_window_size
if ($cw) {
    if ($cw -ge 1000000) {
        $sizeStr = "{0}m" -f ([math]::Round($cw / 1000000, 0))
    } elseif ($cw -ge 1000) {
        $sizeStr = "{0}k" -f ([math]::Round($cw / 1000, 0))
    } else {
        $sizeStr = "$cw"
    }
}

$effort = $data.effort.level
$remaining = $data.context_window.remaining_percentage

$modelPart = "* $model"
if ($sizeStr) { $modelPart += " ($sizeStr context)" }

$line1Parts = @($modelPart)
if ($effort) { $line1Parts += "$effort" }
if ($null -ne $remaining) {
    $remainingRounded = [math]::Round([double]$remaining, 0)
    $line1Parts += "ctx $remainingRounded% left"
}
$line1 = $line1Parts -join " - "

# --- Line 2: current process id ---
$line2 = "pid:$PID"

$lines = @($line1, $line2)

# --- Line 3 (conditional): git repo folder + branch, only when cwd is inside a git repo ---
$cwd = $data.workspace.current_dir
if (-not $cwd) { $cwd = $data.cwd }

if ($cwd -and (Test-Path $cwd)) {
    $gitCheck = git --no-optional-locks -C "$cwd" rev-parse --is-inside-work-tree 2>$null
    if ($gitCheck -eq 'true') {
        $branch = git --no-optional-locks -C "$cwd" rev-parse --abbrev-ref HEAD 2>$null
        $toplevel = git --no-optional-locks -C "$cwd" rev-parse --show-toplevel 2>$null
        $repoName = $null
        if ($toplevel) {
            $repoName = Split-Path -Leaf $toplevel
        }
        if ($repoName -and $branch) {
            $lines += "# $repoName - branch:$branch"
        }
    }
}

Write-Output ($lines -join "`n")
