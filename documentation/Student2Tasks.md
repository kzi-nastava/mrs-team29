##### **2.1.1 Prikaz informacija (Student2)**

Prva stranica koju (neprijavljeni) korisnik vidi je početna stranica aplikacije na kojoj se

mogu videti sva trenutno aktivna vozila sa njihovim položajem na mapi, pri čemu je za svako

vozilo naznačeno da li je zauzeto ili trenutno slobodno.



##### **2.4.2 Notifikacije ulinkovanih putnika (Student2)**

Ulinkovani putnici (ako ih ima) dobijaju mejl i notifikaciju (samo registrovani korisnici u

aplikaciji) da su dodati na vožnju i da je vožnja prihvaćena, u slučaju da je sistem pronašao

podobnog vozača.

Klikom na link u mejlu ili klikom na notifikaciju putnici odlaze na posebnu stranicu gde im

se pruža mogućnost praćenja vožnje (dodatno u 2.6.2).

Kada vozilo stigne na odredište, šalje se ponovo mejl i notifikacija (samo registrovani

korisnici) svim putnicima da je vožnja uspešno završena.



##### **2.6.2 U toku trajanja vožnje (Student2)**

U toku iste vožnje, svaki od putnika je u mogućnosti da pristupi stranici gde im se pruža

mogućnost praćenja vožnje (lokacije vozila na mapi) uz prikaz vremena neophodnog da vozilo

stigne pri čemu se vreme ažurira kako se vozilo približava destinaciji.

U slučaju da vozač ide nekim neadekvatnim putem (po proceni korisnika), svim

putnicima se nudi opcija da prijave nekonzistentnost vozača (kao napomenu). Potrebno je

prikazati malu formu gde se unosi tekst. Ove prijave će se prikazivati u izveštajima i prilikom

pregleda istorija vožnji.



##### **2.7. Završetak vožnje (Student2)**

Nakon što je vožnja obavljena i putnici su izašli iz vozila, vozač označava da je vožnja

gotova i plaćena u samom vozilu. Time vozač prelazi u dostupno stanje, ako nema drugu

zakazanu vožnju. U slučaju postojanja zakazane, učitavaju mu se novi podaci i kreće ka novom

polazištu. Ako nema dodeljenu vožnju, vozač ima opciju da ode na stranicu na kojoj vidi buduće

(zakazane) vožnje. Putnicima stiže mejl i notifikacija o završenoj vožnji, uz mogućnost ocene

vožnje i ponovo mogu poručivati nove vožnje.



##### **2.8. Ocenjivanje vozila i vozača (Student2)**

Nakon završetka, osobi koja je poručila vožnju, nudi se opcija da ocene vozilo, vozača i

ostave komentar. To mogu uraditi odmah nakon vožnje ili odlaskom na pregled istorije vožnji

odakle mogu naknadno da ostave ocenu.

Rok za ostavljanje ocene je 3 dana od završetka vožnje. Ako rok istekne, ista se smatra

neocenjenom.



##### **2.10 Generisanje izveštaja o prethodnim vožnjama (Student1)**

Svi korisnici imaju mogućnost da na osnovu definisanog opsega datuma dobiju grafove

koji prikazuju broj vožnji po danima, broj pređenih kilometara, količinu potrošenog/zarađenog

novca za sopstvene vožnje. Uz te podatke neophodno je prikazati i kumulativnu sumu za opseg

kao i prosek.

Administratori dodatno imaju mogućnost da te podatke vide na jednom grafu za sve

vozače ili putnike, ili da odaberu samo jednu osobu i za nju prikažu podatke.



##### **2.11 Live podrška (Student2)**

U svakom trenutku (bilo da je vožnja u toku ili ne) i vozači i putnici mogu kontaktirati

support za dodatna pitanja. Komunikacija se odvija u obliku chat-a. Administratori su ti koji se

nalaze sa druge strane istog. Chat treba da pamti istoriju tog razgovora, tj. i korisnik i

administrator mogu da vide prethodne poruke. Nije potrebno kreirati novi čet u zavisnosti od

vožnje, nego svaki korisnik ima po jedan chat sa administratorom.



##### **2.13. Pregled stanja vožnje (Student2)**

Administrator može da pregleda stanje vožnje koja trenutno traje, bilo kog vozača. Na

stranici ima pretragu po imenu vozača i odabirom može da pregleda sve informacije, kao što su

vreme polaska, vreme dolaska ili trenutan položaj itd.



##### **2.14. Definisanje cene vožnje (Student2)**

Administrator može da definiše i menja cenu vožnje. Prilikom definisanja treba obratiti

pažnju na tip vozila (standardno, luksuzno, kombi). Nije potrebno pamtiti istoriju cena, neka se u

računima čuvaju podaci o ukupnoj ceni i ceni koja je važila u trenutku kreiranja računa.





