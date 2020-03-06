---
title: Built-In Workloads
weight: 40
---

There are a few built-in workloads which you may want to run. These workloads can be run from a command without having to configure anything, or they can be tailored with their built-in parameters.

This section of the guidebook will explain each of them in detail.

## Built-In Workload Conventions

The built-in workloads follow a set of conventions so that they can
be used interchangeably:

### Phases

Each built-in contains the following tags that can be used to break the workload up into uniform phases:

- schema - selected with `tags=phase:schema`
- rampup - selected with `tags=phase:rampup`
- main - selected with `tags=phase:main`

### Parameters

Each built-in has a set of adjustable parameters which is documented below per workload. For example, the cql-iot workload has a `sources` parameter which determines the number of unique devices in the dataset.


