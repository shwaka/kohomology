import os, subprocess, platform, json
from pathlib import Path
from typing import List, Dict

# This script is written in python since
# - Even when kotlin code is not run at all, this python script can detect it.
# - python is more useful as a script (without creating any project) than javascript and kotlin

def main() -> None:
    sample_dir = get_sample_dir()
    os.chdir(sample_dir)

    failed_sample_list: List[str] = []
    for kotlin_filename in os.listdir(sample_dir / "src/main/kotlin"):
        success = run_sample_in_file(kotlin_filename)
        if not success:
            failed_sample_list.append(kotlin_filename)
    if len(failed_sample_list) > 0:
        print("[Error] The following samples didn't contain expected outputs:")
        for kotlin_filename in failed_sample_list:
            print(kotlin_filename)
        exit(1)

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

def run_sample_in_file(kotlin_filename: str) -> bool:
    print(f"--- Running {kotlin_filename} ---")
    gradlew = get_gradlew_command()
    output = tee(f"{gradlew} run -DsampleName={kotlin_filename}")
    return validate_output(output, kotlin_filename)

def validate_output(output: str, kotlin_filename: str) -> bool:
    data = get_data_for_file(kotlin_filename)
    expected_outputs: List[str] = data["expected_outputs"]
    missed_lines: List[str] = []
    for line in expected_outputs:
        if line not in output:
            missed_lines.append(line)
    if len(missed_lines) > 0:
        print("[Error] The output didn't contain the following expected lines:")
        for line in missed_lines:
            print(line)
    return len(missed_lines) == 0


def get_data_for_file(kotlin_filename: str) -> Dict[str, List[str]]:
    json_file = Path(__file__).parent / "sample.json"
    with open(json_file) as f:
        data = json.load(f)
    sample_name = kotlin_filename.removesuffix(".kt")
    if sample_name in data:
        return data[sample_name]
    else:
        raise Exception(f"Key {sample_name} is not found in {json_file}")

if __name__ == '__main__':
    main()
