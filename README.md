# UDF for Azure Boards ![Master Workflow](https://github.com/takipi/udf-azure-boards/workflows/Master%20Workflow/badge.svg)
This User Defined Function (UDF) sends new OverOps events to Microsft Azure Boards.


## Installation
Instructions on how to install UDFs can be found here:
https://doc.overops.com/docs/user-defined-functions-udfs

Once installed fill out the following parameters

| Parameter    | Required | Description                           |
| ------------ |:--------:| ------------------------------------- |
| authUser     | Yes      | Azure DevOps username                 |
| authToken    | Yes      | Azure DevOps personal access token    |
| organization | Yes      | Azure DevOps username or organization |
| project      | Yes      | Azure DevOps project name             |

Azure DevOps personal access tokens can be created here:
https://dev.azure.com/OverOps/_usersSettings/tokens


## How to build
This is a Maven project.  To build simply run:
```shell
mvn clean install
```
