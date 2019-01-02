# MDReportExtensions

MagicDraw reporting extensions to be used in VTL templates.

## Features

- Sorts Class Names
- Converts HTML to Markdown
- Natural Language Processing

## Creating the JAR

Maven is required.

Be sure to set the `project.magicDrawPath` property to the absolute path of the `reportwizard/lib` directory.

Run the following command

```sh
mvn package
```

Place the JAR located in `target/` in the `{MD_FOLDER}/plugins/com.nomagic.magicdraw.reportwizard/extensions` directory.