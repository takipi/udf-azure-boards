# UDF for Azure Boards ![Master Workflow](https://github.com/takipi/udf-azure-boards/workflows/Master%20Workflow/badge.svg)
This User Defined Function (UDF) sends new OverOps events to Microsft Azure Boards.


## Installation
Instructions on how to install UDFs can be found here:
https://doc.overops.com/docs/user-defined-functions-udfs

Once installed fill out the following parameters

| Parameter    | Required | Description                                                |
| ------------ |:--------:| ---------------------------------------------------------- |
| azureUrl     | No       | Azure Boards URL (for on-prem installations)               |
| authUser     | Yes      | Azure DevOps username                                      |
| authToken    | Yes      | Azure DevOps personal access token                         |
| organization | Yes      | Azure DevOps username or organization                      |
| project      | Yes      | Azure DevOps project name                                  |
| workItemType | No       | Azure Work Item type (Default: Task)                       |
| priority     | No       | Azure Work Item priority (Default: 2)                      |
| dateFormat   | No       | Last seen date format (Default: EEE, M/dd/yyyy HH:mm:ss z) |
| timeZone     | No       | Last seen date time zone (Default: UTC)                    |
| debug        | No       | Debug flag for logging (Default: false)                    |

The `dateFormat` use a Java standard date format.  More information on how to construct a valid date format can be found here: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html

The `timeZone` use Java standard time zones.  A list of valid values can be found here:  https://garygregory.wordpress.com/2013/06/18/what-are-the-java-timezone-ids/ 
Note:  Be mindful of Daylight Savings time.  For example Eastern Standard Time (EST) will not reflect the 1 hour time shift.  To reflect this you will have to use `EST5EDT` instead.



Azure DevOps personal access tokens can be created here:
https://dev.azure.com/OverOps/_usersSettings/tokens


## How to build
This is a Maven project.  To build simply run:
```shell
mvn clean install
```
