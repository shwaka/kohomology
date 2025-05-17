---
title: Repository structure
sidebar_position: 99
---

The repository [shwaka/kohomology](https://github.com/shwaka/kohomology) contains several directories.

## `kohomology/`
The directory `kohomology/` is the main directory and contains the source code of the library.
The library consists of several packages.
The following picture shows the dependency between packages.

![dependency graph](/img/uml/depGraph.png)

The following picture shows some important classes and interfaces
together with their dependency (inheritance and delegation).

![packages](/img/uml/packages.png)

## `profile/`
A project for profiling the library.
Usually a user don't need to see this directory.

## `scripts/`
This directory contains some utility scripts.
Usually a user don't need to see this directory.

## `template/`
This directory serves as a template project to use the library.

## `website/`
The source code for this website.
This directory contains a node project together with some kotlin projects.

### `website/src`, `website/docs`, `website/static`
Directories which are directly used from the node project `website/`.

### `website/sample/`
A project containing source code of samples in this documentation.

### `website/kohomology-js`
A [Kotlin/JS](https://kotlinlang.org/docs/js-overview.html) project which wraps the library.
The page [Calculator](../calculator) is written in TypeScript and uses the library through this project.

### `website/comparison`
Contains projects to compare performance of `kohomology` with that of `sage`.
Results are shown in [this page](./comparison.mdx).
