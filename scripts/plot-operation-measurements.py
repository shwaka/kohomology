from __future__ import annotations

import argparse
from pathlib import Path
from typing import Optional


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Plot operation measurements exported by OperationLogger.getMeasurementsCSV().",
    )
    parser.add_argument("csv_file", type=Path)
    parser.add_argument("-o", "--output", type=Path, default=None)
    parser.add_argument("--x-column", default="size")
    parser.add_argument("--y-column", default="duration_ms")
    parser.add_argument("--operation", action="append", default=None)
    parser.add_argument("--log-x", action="store_true")
    parser.add_argument("--log-y", action="store_true")
    parser.add_argument("--show", action="store_true")
    return parser.parse_args()


def get_default_output_path(csv_file: Path, x_column: str, y_column: str) -> Path:
    return csv_file.with_name(f"{csv_file.stem}-{x_column}-{y_column}.png")


def require_column(data: pd.DataFrame, column: str) -> None:
    if column not in data.columns:
        available_columns = ", ".join(data.columns)
        raise ValueError(f"Column '{column}' is not found. Available columns: {available_columns}")


def prepare_data(
    csv_file: Path,
    x_column: str,
    y_column: str,
    operations: Optional[list[str]],
) -> pd.DataFrame:
    import pandas as pd

    data = pd.read_csv(csv_file)
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
    output = args.output or get_default_output_path(args.csv_file, args.x_column, args.y_column)
    data = prepare_data(
        csv_file=args.csv_file,
        x_column=args.x_column,
        y_column=args.y_column,
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
