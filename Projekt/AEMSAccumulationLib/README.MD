# AEMS Aufsummierer (Accumulator)
Diese kleine Klassenbibliothek ermöglicht das einfache Aufsummieren von viertelstündlichen Verbrauchswerten auf größere Einheiten, wie z.B.
Tages- oder Wochenwerte. Aber die Einfachheit trügt! Die viertelstündlichen Werte müssen folgende Vorraussetzungen erfüllen:

- Die Werte müssen kontinuierlich sein. Wenn z.B. alle Jänner und März Werte vorhanden sind, aber die Februar Werte fehlen, führt dies zu einem
Falschen Ergebnis
- Wenn es also erst Werte ab z.B. April gibt, dann muss das bei der Programmierung berücksichtigt werden.

## Verwendung
Die viertelstündlichen Werte müssen in einer `NavigableMap<java.sql.Timestamp, Double>` vorliegen. Eine Klasse, welche dieses Interface
implementiert, wäre die `TreeMap`. 
Wenn dies gegeben ist, kann mithilfe des `TimePeriod` Enums aufsummiert werden.
```java
NavigableMap<Timestamp, Double> values = new TreeMap<Timestamp, Double>();
fillMap(values);
Double[] accumulations = TimePeriod.WEEKLY.accumulate(values);
// In accumulations stehen die Werte von Montag - Sonntag
// accumulations[0] = Wert für Montag
// accumulations[1] = Wert für Dienstag usw.
// Ist ein Wert null, so sind für diesen Tag/Monat etc. keine Daten vorhanden.
```

## Maven
```xml
<dependency>
  <groupId>at.aems</groupId>
  <artifactId>accumulator</artifactId>
  <version>1.0</version>
</dependency>
```