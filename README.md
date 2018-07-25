# MDReportExtensions

MagicDraw reporting extensions to be used in VTL templates.

## Creating the JAR

Maven is required. Run the following command

```sh
mvn clean compile assembly:single
```

Place the JAR located in `target/` in the `{MD_FOLDER}/plugins/com.nomagic.magicdraw.reportwizard/extensions` directory.