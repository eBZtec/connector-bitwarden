# Connector for Bitwarden Enterprise

This connector is a tool for provisioning Members, Groups and Collections using the Bitwarden Public RESTful API.

## Capabilities and Features

| Features          |         |                                                               |
|-------------------|---------|---------------------------------------------------------------|
| Schema            | YES     | Members, Groups and Collections                               |
| Live Sync         | NO      |                                                               |
| Password          | NO      |                                                               |
| Activation        | YES     |                                                               |
| Filtering         | PARTIAL | Limited to the Bitwarden API REST                             |
| Native Attributes | YES     | Use ri:email instead icfs:name. Use ri:id instead of icfs:uid |
| Provisioning      | YES     | Members, Groups and Collections (except creation)             |

## Interoperability

This connector works with the latest Public [RESTful API](https://bitwarden.com/help/api/) for Bitwarden Enterprise, and it can authenticate with cloud-hosted or self-hosted servers.

The authentication uses as OAuth2 Client credentials application, and requires an access token to manage the endpoints.

For more information about how to generate the client_id and client_secret, please access the Bitwarden help [page](https://bitwarden.com/help/public-api/) for the public API.

## Limitations

- The connector accepts only 'EqualsFilter' for 'id' attribute in all object types. Except for 'groups' object where you can search all members by 'ContainsAllValuesFilter'.
- The API has some limitations:
  - Create collections are not supported;
  - The GET collections endpoint does not return the name of the collection (why??).

## Schemas

### Members

  | Attribute                        | Type    | Required | Description                                                   |
  |----------------------------------|---------|----------|---------------------------------------------------------------|
  | id                               | String  | No       |                                                               |
  | name                             | String  | No       |                                                               |
  | email                            | String  | Yes      |                                                               |
  | twoFactorEnabled                 | Boolean | No       |                                                               |
  | status                           | Integer | Yes      |                                                               |
  | resetPasswordEnrolled            | Boolean | No       |                                                               |
  | ssoExternalId                    | String  | No       |                                                               |
  | type                             | Integer | Yes      | 0 - Owner, 1 - Administrator, 2 - User, 3 - Custom Permission |
  | externalId                       | String  | No       |                                                               |
  | groups                           | String  | No       | Contains all member's Group IDs                               |
  | permissions.accessEventLogs      | Boolean | No       | Used only for custom members type.                            |
  | permissions.accessImportExport   | Boolean | No       | Used only for custom members type.                            |
  | permissions.accessReports        | Boolean | No       | Used only for custom members type.                            |
  | permissions.createNewCollections | Boolean | No       | Used only for custom members type.                            |
  | permissions.editAnyCollection    | Boolean | No       | Used only for custom members type.                            |
  | permissions.deleteAnyCollection  | Boolean | No       | Used only for custom members type.                            |
  | permissions.manageGroups         | Boolean | No       | Used only for custom members type.                            |
  | permissions.managePolicies       | Boolean | No       | Used only for custom members type.                            |
  | permissions.manageSso            | Boolean | No       | Used only for custom members type.                            |
  | permissions.manageUsers          | Boolean | No       | Used only for custom members type.                            |
  | permissions.manageResetPassword  | Boolean | No       | Used only for custom members type.                            |
  | permissions.manageScim           | Boolean | No       | Used only for custom members type.                            |

### Groups

| Attribute   | Type    | Required | Description                                                                              |
|-------------|---------|----------|------------------------------------------------------------------------------------------|
| id          | String  | No       |                                                                                          |
| name        | String  | Yes      |                                                                                          |
| externalId  | String  | No       |                                                                                          |
| members     | String  | No       | Contains all member's IDs                                                                |
| collections | String  | No       | Contains all collection's IDs . Format "id=<collection guid>;ro=<0/1>;hp=<0/1>;mg=<0/1>" |

ro => readOnly
hp => hidePassword
mg = manage

### Collections

| Attribute  | Type    | Required | Description                                                                   |
|------------|---------|----------|-------------------------------------------------------------------------------|
| id         | String  | No       |                                                                               |
| name       | String  | Yes      |                                                                               |
| externalId | String  | No       |                                                                               |
| groups     | String  | No       | Contains all group's IDs. Format "id=<group guid>;ro=<0/1>;hp=<0/1>;mg=<0/1>" |


## Future

Implement [Association By Reference](https://docs.evolveum.com/midpoint/devel/design/associations-4.9/) for Groups and Collections and vice versa.

## Build

### Maven

* Download Connector for Bitwarden source code from Github.
* build connector with maven:
```
mvn clean install -DskipTests=true
```
* find connector-bitwarden-{version}.jar in ```/target``` folder

### Installation

* Copy/move connector-bitwarden-{version}.jar to ```{midPoint_home}/connid-connectors/``` directory

### Run tests

Export the environment variables:

```
export BW_BASE_URL="https://api.bitwarden.com"
export BW_AUTH_URL="https://identity.bitwarden.com"
export BW_CLIENT_ID="<YOUR CLIENT ID>"
export BW_CLIENT_SECRET="<YOUR CLIENT SECRET>"
```

and then run:

```bash
mvn clean install
```
