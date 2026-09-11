# URL Shortener

Java ve Spring Boot ile geliştirilen, uzun bağlantıları kısa ve paylaşılabilir adreslere dönüştüren bir web uygulaması.

Canlı adres: `https://go.abdullahaltun.com.tr`

## Teknolojiler

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Maven
- Flyway
- Docker

## Hedeflenen özellikler

- Uzun URL için benzersiz kısa kod oluşturma
- Kısa bağlantıdan asıl adrese yönlendirme
- İsteğe bağlı son kullanma tarihi
- Geçersiz bağlantılar için uygun hata cevapları
- Tıklanma istatistikleri
- Otomatik testler
- Docker ile çalıştırma

## Durum

Proje geliştirme aşamasındadır.

## Yerel geliştirme

Örnek ortam dosyasını kopyalayın ve tüm uygulamayı Docker ile başlatın:

```bash
cp .env.example .env
docker compose up -d --build
```

Uygulama `http://localhost:8080` adresinde çalışır.
Sağlık durumu `http://localhost:8080/actuator/health` adresinden kontrol edilir.

Spring Boot'u Docker dışında geliştirirken yalnızca PostgreSQL'i başlatın:

```bash
docker compose up -d postgres
./mvnw spring-boot:run
```

Testleri çalıştırın:

```bash
./mvnw test
```

Production ortamında veritabanı ve sunucu ayarları environment variable olarak verilmelidir.
