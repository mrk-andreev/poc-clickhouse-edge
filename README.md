# POC Clickhouse Edges

![GitHub](https://img.shields.io/github/license/mrk-andreev/poc-clickhouse-edge?style=flat-square)
![Travis (.org) branch](https://img.shields.io/travis/mrk-andreev/poc-clickhouse-edge/master?style=flat-square)
![CodeQualityScore](https://www.code-inspector.com/project/27245/score/svg)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/1d8ccc89390946ccab2d96831535bdf7)](https://www.codacy.com/gh/mrk-andreev/poc-clickhouse-edge/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=mrk-andreev/poc-clickhouse-edge&amp;utm_campaign=Badge_Grade)
[![CodeFactor](https://www.codefactor.io/repository/github/mrk-andreev/poc-clickhouse-edge/badge)](https://www.codefactor.io/repository/github/mrk-andreev/poc-clickhouse-edge)
[![DeepScan grade](https://deepscan.io/api/teams/15308/projects/18464/branches/453471/badge/grade.svg)](https://deepscan.io/dashboard#view=project&tid=15308&pid=18464&bid=453471)
[![DeepSource](https://deepsource.io/gh/mrk-andreev/poc-clickhouse-edge.svg/?label=active+issues&show_trend=true)](https://deepsource.io/gh/mrk-andreev/poc-clickhouse-edge/?ref=repository-badge)


Proof of concept clickhouse edge installation.

## Overview

- Data locality driven architecture
- Coordinator main deployment server in Public cloud
- Multiple Edge deployments with local DB (Clickhouse local)

![](./docs/assets/SolutionComponents.drawio.png)

- `src/app-coordinator` Coordinator application
- `src/ui-coordinator` UI for coordinator
- `src/app-edge` Edge application
- `src/deploy` Deployment scripts

## Tech stack

- Spring Framework 5.3
- Apache Camel 3.11
- Angular 12
- RxJS 6
