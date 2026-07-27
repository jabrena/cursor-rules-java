# Maintenance

Some **User prompts** designed to help in the maintenance of this repository.

## Begin a new release

### Bump a new Snapshot version

```bash
# Maven command to update the maven version to next minor version
./mvnw versions:set -DnewVersion=0.19.0-SNAPSHOT
./mvnw versions:commit

#Bump to a new snapshot
@skill-resources/src/main/resources/ update version to 0.19.0-SNAPSHOT finally regenerate local skills with ./mvnw clean install -pl plinth-skills-generator -am

#Update the XML Schema to latest version
@skill-resources/src/main/resources/ update all XML Schema with XSD Configuration pointing to PML 0.8.0 and regenerate local skills with ./mvnw clean install -pl plinth-skills-generator
```

## Finish a release

```bash
# Update @All-JEPS.md
Update @All-JEPS.md with JEPs about Java 26 from https://openjdk.org/jeps/0

# Prompt to update some cursor rules with ideas included in JEPS
Can you analyze the last Java version, Java 26 from @All-JEPS.md if exist some JEP that it could be possible to be added as example in one of the XML documents from generator project. Only analyze, not create any new example and show a summary from the analysis.

# Prompt to update the list
Review that the list doesn´t any broken link to @/.cursor with .md files

# Prompt to provide a release changelog
Can you update the current changelog for 0.18.0 comparing git commits in relation to 0.17.0 tag. Use  @https://keepachangelog.com/en/1.1.0/  rules

#Bump to a new snapshot
Update version to 0.18.0 for all XML files and pom.xml in all maven modules and finally regenerate local skills with ./mvnw clean install -pl plinth-skills-generator -am

```

## Release process

- [ ] Update CHANGELOG.md
- [ ] Remove SNAPSHOT from .xml, .md & pom.xml
- [ ] Review git changes for hidden issues (Manual) https://github.com/jabrena/plinth/compare/0.16.0...feature/release-0170
- [ ] Verify if all features were tested propertly
- [ ] Review if Agents need to add more Skills
- [ ] Review Skill validation output
- [ ] Review Skill security validation
- [ ] Last review in docs (Manual)
- [ ] Optionally validate generated site HTML with Nu Html Checker (see [Validate site HTML](#validate-site-html-on-demand))
- [ ] Refresh public skills/ release output with `./mvnw clean install -pl plinth-skills-generator -P release`
- [ ] Verify that Pipeline is in Green

---

- [ ] Update Skills Registry
- [ ] Tag repository
- [ ] Create article
- [ ] Communicate in social media

---


```bash
# Review Skill registries
https://github.com/jabrena/plinth
https://tessl.io/registry/skills/submit
npx tessl skill review ./skills/xxx
cd target && npx skills add jabrena/plinth --all --agent cursor && cd ..
```

---

```bash
# Prompt to provide a release changelog
Can you update the current changelog for 0.14.0 comparing git commits in relation to 0.13.0 tag. Use  @https://keepachangelog.com/en/1.1.0/  rules

# Maven command to update the maven version to next minor version
./mvnw versions:set -DnewVersion=0.18.0
./mvnw versions:commit

# Prompt to update the project to a new version
Update xml files from @resources/ and update the version to 0.18.0 removing Snapshot.
./mvnw clean verify -pl plinth-skills-generator -am

## Tagging process
git tag --list
git tag 0.18.0
git push --tags
```

## Improve skills

```
solving the problem with the skill, did you learn something that it didn´t work as expected or something to improve in the skill?
```

## Validate site HTML (on demand)

After regenerating the GitHub Pages site into `docs/` (`./mvnw clean generate-resources -pl site-generator -P site-update`), you can run the [Nu Html Checker (vnu)](https://validator.github.io/validator/) manually with JBang. This is **not** part of the default Maven build; use it when reviewing site/HTML quality.

Requires [JBang](https://www.jbang.dev/) on `PATH`. First run may prompt to trust the validator catalog.

```bash
# Validate only *.html / *.htm / *.xhtml under docs/ (skip feeds, images, and other non-HTML)
jbang vnu@validator/validator --skip-non-html --format text docs
```

Useful options:

```bash
# Errors only (quieter)
jbang vnu@validator/validator --skip-non-html --errors-only --format text docs

# Single page
jbang vnu@validator/validator --skip-non-html --format text docs/index.html
```

Without `--skip-non-html`, pointing vnu at the `docs/` directory also tries to parse Atom feeds, images, and other assets as HTML and produces large amounts of noise.