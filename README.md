# The N Philosophers


### Wymagania

- Zainstalowana JDK 11 (lub wyższa, testowane było na 11, bo to obecny LTS).
- Należy pamiętać:
  - o ustawieniu zmiennej środowiskowej `JAVA_HOME` wskazującej
    na katalog gdzie zainstalowaliśmy JDK.
  - oraz updejtowaniu zmiennej środowiskowej `PATH` tak by zawierała w sobie
    również ścieżkę do `${JAVA_HOME}/bin`

- Projekt jest budowany maven'em. Używamy maven-wrappera. Proszę zwrócić uwagę, że poniżej
  używamy komendy `mvnw` a nie `mvn`. W związku z tym instalacja lokalna mavena nie jest
  potrzebna (lepiej użyć maven-wrappera, ze względu na ryzyko niezgodności wersji).

- **Połączenie internetowe** - maven ściąga zadeklarowane zależności z internetu
  podczas pierwszego builda.


### Struktura katalogu

Struktura katalogów wygląda tak:

```
-- .mvn
-- src
   -- main
      -- java
      -- resources
-- .gitgnore
-- mvnw
-- mvnw.cmd
-- pom.xml
-- README.md
```

- Źródła programu są w podkatalogoach `/src/main`.
- Z kolei `/src/test` zawiera źródła unit testów.
- Podkatalog `.mvn` oraz pliki `mvnw` i `mvnw.cmd` to maven-wrapper.
- Plik `pom.xml` to deskryptor projektu i jego zależności dla mavena.


### Praca z maven'em

Wszystkie komendy poniżej wykonujemy z katalogu głównego projektu
(czyli tam gdzie jest plik `pom.xml` oraz ten plik - `README.md`).


### 1. Budowanie projektu

Wchodzimy w terminalu do katalogu głównego projektu (czyli tam gdzie 
jest plik `pom.xml`).

Jeśli jesteśmy na linux-ie, należy pamiętać by `mvnw` był wykonywalny:

```
chmod +x mvnw
```

Następnie z linii poleceń wykonujemy:

```
./mvnw package
```

UWAGA:
Pierwszy build ściąga zależności z Internetu. Kolejne buildy będą znacznie szybsze,
ponieważ nie będzie już ściągania zależności.


### Uruchamianie programu

Zbudowany artefakt znajduje się w podkatalogu `target` głównego folderu projektu.
Jest to plik: `target/philosophers.jar`

Uruchamiamy go (z głównego folderu projektu):

```
java -jar target/philosophers.jar --help
```

Opcja `--help` wyświetla manual naszego programu.

Aby uruchomić program w wersji graficznej, wystarczy po prostu:

```
java -jar target/philosophers.jar
```

Wygenerowany raport znajduje się w katalogu, w którym uruchomiliśmy program i 
nazywa się `report.pdf`.  

### Czyszczenie projektu

Aby usunąć wszystkie zbudowane artefakty oraz wszystkie pliki pomocnicze utworzone 
w trakcie build'a uruchamiamy:

```
./mvnw clean
```