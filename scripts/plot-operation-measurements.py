#!/usr/bin/env -S uv run --script
# /// script
# requires-python = ">=3.10"
# dependencies = [
#   "matplotlib",
#   "pandas",
# ]
# ///
#
# Usage:
#   1. Publish the current kohomology snapshot to mavenLocal:
#        cd kohomology
#        ./gradlew publishToMavenLocal
#
#   2. Export operation measurements:
#        cd ../profile
#        ./gradlew exportOperationMeasurementsCSV
#
#      To select a target non-interactively:
#        ./gradlew exportOperationMeasurementsCSV -DmeasurementTarget=4
#
#      To change the output directory:
#        ./gradlew exportOperationMeasurementsCSV \
#          -DmeasurementTarget=4 \
#          -DmeasurementOutputDir=/tmp/kohomology-operation-measurements
#
#   3. Plot the exported CSV:
#        cd ..
#        scripts/plot-operation-measurements.py \
#          profile/build/kohomology/operation-measurements/matrix-operations.csv
#
#      For a log-log plot using work_size:
#        scripts/plot-operation-measurements.py \
#          profile/build/kohomology/operation-measurements/matrix-operations.csv \
#          --x-column work_size --log-x --log-y
#
#      To aggregate multiple runs by row index:
#        scripts/plot-operation-measurements.py \
#          profile/build/kohomology/operation-measurements/*_matrix-operations_*.csv \
#          --x-column work_size --aggregate-stat median --log-x --log-y

from __future__ import annotations

import argparse
from pathlib import Path
from typing import Optional


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Plot operation measurements exported by OperationLogger.getMeasurementsCSV().",
    )
    parser.add_argument("csv_file", type=Path, nargs="+")
    parser.add_argument("-o", "--output", type=Path, default=None)
    parser.add_argument("--x-column", default="size")
    parser.add_argument("--y-column", default="duration_ms")
    parser.add_argument(
        "--aggregate-stat",
        choices=["median", "mean", "min", "max", "p90", "p95"],
        default="median",
        help="Statistic used when multiple CSV files are passed.",
    )
    parser.add_argument("--operation", action="append", default=None)
    parser.add_argument("--log-x", action="store_true")
    parser.add_argument("--log-y", action="store_true")
    parser.add_argument("--show", action="store_true")
    return parser.parse_args()


def get_default_output_path(
    csv_files: list[Path],
    x_column: str,
    y_column: str,
    aggregate_stat: str,
) -> Path:
    first_csv_file = csv_files[0]
    if len(csv_files) == 1:
        suffix = f"{x_column}-{y_column}"
    else:
        suffix = f"{x_column}-{y_column}-{aggregate_stat}-{len(csv_files)}runs"
    return first_csv_file.with_name(f"{first_csv_file.stem}-{suffix}.png")


def require_column(data: pd.DataFrame, column: str) -> None:
    if column not in data.columns:
        available_columns = ", ".join(data.columns)
        raise ValueError(f"Column '{column}' is not found. Available columns: {available_columns}")


def read_csv(csv_file: Path) -> pd.DataFrame:
    import pandas as pd

    data = pd.read_csv(csv_file)
    data["measurement_index"] = data.index
    return data


def numeric_value_columns(data: pd.DataFrame, y_column: str) -> list[str]:
    return [
        column
        for column in data.columns
        if column not in ["operation", y_column, "measurement_index", "run_index"]
    ]


def validate_same_measurements(data_list: list[pd.DataFrame], y_column: str) -> None:
    first_data = data_list[0]
    compared_columns = ["operation"] + numeric_value_columns(first_data, y_column)

    for index, data in enumerate(data_list[1:], start=1):
        if list(data.columns) != list(first_data.columns):
            raise ValueError(f"CSV #{index} has different columns from the first CSV.")
        if len(data) != len(first_data):
            raise ValueError(
                f"CSV #{index} has {len(data)} rows, but the first CSV has {len(first_data)} rows."
            )
        for column in compared_columns:
            if not data[column].equals(first_data[column]):
                raise ValueError(f"CSV #{index} differs from the first CSV at column '{column}'.")


def aggregate_duration(data: pd.Series, aggregate_stat: str) -> float:
    if aggregate_stat == "median":
        return data.median()
    if aggregate_stat == "mean":
        return data.mean()
    if aggregate_stat == "min":
        return data.min()
    if aggregate_stat == "max":
        return data.max()
    if aggregate_stat == "p90":
        return data.quantile(0.9)
    if aggregate_stat == "p95":
        return data.quantile(0.95)
    raise ValueError(f"Unsupported aggregate_stat: {aggregate_stat}")


def aggregate_by_index(
    data_list: list[pd.DataFrame],
    y_column: str,
    aggregate_stat: str,
) -> pd.DataFrame:
    import pandas as pd

    validate_same_measurements(data_list, y_column)
    combined = pd.concat(
        [
            data.assign(run_index=run_index)
            for run_index, data in enumerate(data_list)
        ],
        ignore_index=True,
    )
    first_data = data_list[0]
    group_columns = ["measurement_index", "operation"] + numeric_value_columns(first_data, y_column)
    aggregated = (
        combined
        .groupby(group_columns, dropna=False, as_index=False)
        .agg(
            **{
                y_column: (
                    y_column,
                    lambda series: aggregate_duration(series, aggregate_stat),
                ),
                "run_count": (y_column, "count"),
            }
        )
    )
    expected_run_count = len(data_list)
    if not (aggregated["run_count"] == expected_run_count).all():
        raise ValueError("Some measurements were not aggregated across all runs.")
    return aggregated.drop(columns=["run_count"])


def prepare_data(
    csv_files: list[Path],
    x_column: str,
    y_column: str,
    aggregate_stat: str,
    operations: Optional[list[str]],
) -> pd.DataFrame:
    import pandas as pd

    data_list = [read_csv(csv_file) for csv_file in csv_files]
    data = data_list[0] if len(data_list) == 1 else aggregate_by_index(data_list, y_column, aggregate_stat)
    require_column(data, "operation")
    require_column(data, x_column)
    require_column(data, y_column)

    data[x_column] = pd.to_numeric(data[x_column], errors="coerce")
    data[y_column] = pd.to_numeric(data[y_column], errors="coerce")
    data = data.dropna(subset=[x_column, y_column])

    if operations is not None:
        data = data[data["operation"].isin(operations)]

    if data.empty:
        raise ValueError("No rows remain after filtering and dropping empty numeric values.")

    return data.sort_values(["operation", x_column])


def plot_data(
    data: pd.DataFrame,
    output: Path,
    x_column: str,
    y_column: str,
    log_x: bool,
    log_y: bool,
    show: bool,
) -> None:
    import matplotlib.pyplot as plt

    fig, ax = plt.subplots(figsize=(10, 6))

    for operation, operation_data in data.groupby("operation", sort=True):
        ax.plot(
            operation_data[x_column],
            operation_data[y_column],
            marker="o",
            linewidth=1.5,
            label=operation,
        )

    ax.set_xlabel(x_column)
    ax.set_ylabel(y_column)
    ax.set_title(f"{y_column} by {x_column}")
    ax.grid(True, which="both", alpha=0.3)
    ax.legend()

    if log_x:
        ax.set_xscale("log")
    if log_y:
        ax.set_yscale("log")

    fig.tight_layout()
    fig.savefig(output, dpi=160)
    print(f"Wrote {output}")

    if show:
        plt.show()


def main() -> None:
    args = parse_args()
    output = args.output or get_default_output_path(
        args.csv_file,
        args.x_column,
        args.y_column,
        args.aggregate_stat,
    )
    data = prepare_data(
        csv_files=args.csv_file,
        x_column=args.x_column,
        y_column=args.y_column,
        aggregate_stat=args.aggregate_stat,
        operations=args.operation,
    )
    plot_data(
        data=data,
        output=output,
        x_column=args.x_column,
        y_column=args.y_column,
        log_x=args.log_x,
        log_y=args.log_y,
        show=args.show,
    )


if __name__ == "__main__":
    main()
