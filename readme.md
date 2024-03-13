# Charakterystyka danych
W ramach wielu komisów samochodowych na całym świecie rejestrowane są dane dotyczące sprzedanych aut.

W strumieniu pojawiają się zdarzenia zgodne ze schematem `CarSaleEvent`.

```
create json schema CarSaleEvent(brand string, `year` int,
mileage int, price int, ets string, its string)
```

Każde zdarzenie związane z jest z faktem sprzedaży samochodu danej marki,
o danym roczniku, z określonym przebiegiem, za daną cenę.

Dane uzupełnione są o dwie etykiety czasowe.
* Pierwsza (`ets`) związana jest z momentem sprzedaży auta.
  Etykieta ta może się losowo spóźniać w stosunku do czasu systemowego maksymalnie do 30 sekund.
* Druga (`its`) związana jest z momentem rejestracji zdarzenia w systemie.

# Opis atrybutów

Atrybuty w każdym zdarzeniu zgodnym ze schematem `CarSaleEvent` mają następujące znaczenie:

* `brand` - marka auta
* `year` - rok produkcji
* `mileage` - liczba przejechanych kilometrów
* `price` - cena, jaką zapłacono za dane auto
* `ets` - czas sprzedaży
* `its` - czas rejestracji faktu sprzedaży

# Zadania
Opracuj rozwiązania poniższych zadań.
* Opieraj się strumieniu zdarzeń zgodnych ze schematem `CarSaleEvent`
* W każdym rozwiązaniu możesz skorzystać z jednego lub kilku poleceń EPL.
* Ostatnie polecenie będące ostatecznym rozwiązaniem zadania musi
  * być poleceniem `select`
  * posiadającym etykietę `answer`, przykładowo:

```aidl
@name('answer') SELECT brand, price, mileage, year, ets, its
FROM CarSaleEvent#ext_timed(java.sql.Timestamp.valueOf(its).getTime(), 3 sec)
```

## Zadanie 1
Utrzymuj informacje średniej ceny sprzedanych samochodów danej marki zarejestrowanych w ciągu ostatnich 10 sekund.

Wyniki powinny zawierać następujące kolumny:
- `brand` - nazwę marki oraz
- `avgprice` - średnią cenę sprzedanych samochodów danej marki w ciągu ostatnich 10 sekund.

## Zadanie 2
Chcemy znajdować okazje. Dlatego wykrywaj przypadki sprzedania auta z przebiegiem mniejszym niż 10 000 i ceną poniżej 20 000.

Wyniki powinny zawierać następujące kolumny:
- `brand` - nazwa marki
- `year` - rok produkcji sprzedanego samochodu
- `mileage` - przebieg sprzedanego samochodu
- `price` - cena sprzedanego samochodu.

## Zadanie 3
Analizując tylko samochody o roczniku mniejszym niż 2015, wykrywaj przypadki sprzedaży auta, którego cena była 2 razy mniejsza niż średnia cena samochodów danej marki w ciągu ostatnich 20 sekund.

Wyniki powinny zawierać, następujące kolumny:
- `brand` - nazwę marki
- `year` - rok produkcji sprzedanego samochodu
- `mileage` - przebieg sprzedanego samochodu
- `price` - cenę sprzedanego samochodu
- `avgprice` - średnią cenę sprzedanych samochodów o roczniku mniejszym niż 2015 tej samej marki w ciągu ostatnich 20 sekund

## Zadanie 4
Znajduj przypadki sprzedaży samochodów o roczniku mniejszym niż 2015 i przebiegu mniejszym niż 20 000, których cena jest mniejsza niż średnia cena wszystkich samochodów tej samej marki.

Wyniki powinny zawierać, następujące kolumny:
- `brand` - nazwę marki
- `year` - rok produkcji sprzedanego samochodu
- `mileage` - przebieg sprzedanego samochodu
- `avgPrice` - średnia cena wszystkich aut tej samej marki

## Zadanie 5
Wyszukuj serie co najmniej dwóch sprzedaży Fordów za kwotę mniejszą niż 20 000 trwającą nie dłużej niż 5 sekund, w trakcie której nie dokonano sprzedaży żadnej Toyoty.

Wyniki powinny zawierać, następujące kolumny:
- `its1` - data rejestracji sprzedaży pierwszego Forda
- `price1` - kwota sprzedaży pierwszego Forda
- `its2` - data rejestracji sprzedaży drugiego Forda
- `price2` - kwota sprzedaży drugiego Forda

## Zadanie 6
Jesteśmy klientem, którego interesują albo bardzo tanie Fordy, albo bardzo drogie Fordy, nie interesuje nas nic pośrodku. Szukamy przypadków sprzedaży 3 kolejnych Fordów, których cena jest albo mniejsza niż 50 000 zł, albo większa niż 300 000 zł, a różnice cen pomiędzy następującymi po sobie sprzedażami nie przekraczają 20 000 zł.

Wyniki powinny zawierać, następujące kolumny:
- `priceA` - cena pierwszego samochodu
- `priceB` - cena drugiego samochodu
- `priceC` - cena trzeciego samochodu
- `yearA` - rok produkcji pierwszego samochodu
- `yearB` - rok produkcji drugiego samochodu
- `yearC` - rok produkcji trzeciego samochodu

## Zadanie 7
Dla każdej marki samochodu znajduj serie składające się z co najmniej 4 sprzedaży, w których każda następna jest za kwotę mniejszą od poprzedniej.

Wyniki powinny zawierać, następujące kolumny:
- `brand` - nazwę marki
- `start_price` - cena sprzedaży pierwszego auta w serii
- `end_price` - cena sprzedaży ostatniego auta w serii

