import os, subprocess, platform
from pathlib import Path
from typing import List

def main() -> None:
    sample_dir = get_sample_dir()
    os.chdir(sample_dir)

    for kotlin_filename in os.listdir(sample_dir / "src/main/kotlin"):
        run_sample_with_name(kotlin_filename)

def get_sample_dir() -> Path:
    git_root_dir: Path = Path(os.popen("git rev-parse --show-toplevel").read().removesuffix("\n"))
    return git_root_dir / "website/sample"

def get_gradlew_command() -> str:
    system: str = platform.system()
    if system == "Linux" or system == "Darwin":
        return "./gradlew"
    elif system == "Windows":
        return "gradlew.bat"
    else:
        raise Exception(f"Unknown system: {system}")

def tee(cmd: str) -> str:
    proc = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    stdout = proc.stdout
    lines: List[str] = []
    if stdout is None:
        raise Exception("stdout is None")
    for line in stdout:
        line_decoded: str = line.decode("utf-8")
        print(line_decoded, end="")
        lines.append(line_decoded)
    return "".join(lines)

def run_sample_with_name(kotlin_filename: str) -> None:
    gradlew = get_gradlew_command()
    output = tee(f"{gradlew} run -DsampleName={kotlin_filename}")

if __name__ == '__main__':
    main()
