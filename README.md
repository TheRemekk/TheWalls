# TheWalls

Plugin do gry Minecraft. Stworzony z użyciem IntelliJ i wtyczki Bukkit.

## Spis treści

1. [Opis](#l1)
2. [Funkcjonalność](#l2)

<a id="l1"></a>
## Opis

TheWalls to krótka rozgrywka w której gracze wybierają jeden z możliwych zestawów startowych, a następnie dzielą się między sobą na cztery drużyny na przygotowanej wcześniej arenie, mają odpowiednią ilość czasu na przygotowanie odpowiedniego wyposażenia.

Następnie mury pomiędzy drużynami opadają i rozpoczyna się walka z której wyłoniona zostanie tylko jedna zwycięzka drużyna. Drużyną wygrywającą rozgrywkę jest ta, która jako jedyna nie straci wszystkich graczy z zespołu. Jeżeli gra nie zakończy się przed upływem limitu czasu rozgrywka pozostaje nierozstrzygnięta. 

#### Przedmioty w czasie przygotowań można zdobyć poprzez:
* otwieranie skrzyń, na mapce mogą znajdywać się ich dwa rodzaje:
    * skrzynie, które posiadają wcześniej ustawione przedmioty
    * skrzynie, które po kliknięciu w nie lewym/prawym przyciskiem myszy znikają, a następnie wrzucają do ekwipunku losową ilość kilku z możliwych do zdobycia przedmiotów
* wykopywanie kamienia - w rozgrywce tej z kamienia wypadają niezbędne surowce (tzw. drop ze stone'a)

<a id="l2"></a>

## Funkcjonalność
Plugin był testowany jedynie na wersji 1.16.1

#### Do poprawnego działania TheWalls potrzebne są trzy pluginy:
* WorldGuard
* WorldEdit
* (opcjonalnie) Multiverse-Core - nie jest konieczny, ale może pomóc w modyfikacji areny.

Potrzebny jest również świat, który służy do generowania modelu areny podczas tworzenia aren.
Zamieszczony będzie on przy każdym wydaniu nowej wersji. 

Wystarczy świat w formacie .zip wypakować, a następnie folder ze światem wrzucić do plików serwerowych.

W pliku config.yml można edytować parametry takie jak długość każdej fazy rozgrywki, nazwy aren, ilość graczy jaką każda z aren oraz drużyn może posiadać, oraz szansę na wypadnięcie każdego z surowców z kamienia.

#### Uwaga! Nie zalecam edytować innych parametrów pliku konfiguracyjnego


