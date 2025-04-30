# Stillingsfeed Admin

Et enkelt admin-grensesnitt mot [pam-stilling-feed](https://github.com/navikt/pam-stilling-feed) for å administrere konsumenter og tokens.
Bruker Kotlin sitt [HTML DSL](https://github.com/Kotlin/kotlinx.html), [HTMX](https://htmx.org/) og [Javalin](https://javalin.io/).

## Lokal kjøring

1. Bytt til korrekt Java SDK, ved å bruke kommandoen `sdk env`.
1. Bygg appen med gralde: `./gradlew build`.
1. Kjør `LocalApplication.kt` for å starte applikasjonen.
