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

## `sample/`
A project containing source code of samples in this documentation.

## `scripts/`
This directory contains some utility scripts.
Usually a user don't need to see this directory.

## `template/`
This directory serves as a template project to use the library.

## `website/`
The source code for this website.
