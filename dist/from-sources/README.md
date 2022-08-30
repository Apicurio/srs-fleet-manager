
# Start Multitenant Apicurio Registry infrastructure from sources [dev-mode]

To download the repositories from git and compile them run:

```bash
./download.sh
```
required every time there are source changes in the upstream repos.

build the projects:
```bash
./build.sh
```
required once after `download.sh` or for local changes.

and finally run the infrastructure:
```bash
./run-all.sh
```
run all the services, press Ctrl+C to kill all the sub processes.



Evantually, clean the target folder:
```bash
./clean.sh
```
run all the services, press Ctrl+C to kill everything.
