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

Örnek ortam dosyasını kopyalayın ve PostgreSQL'i başlatın:

```bash
cp .env.example .env
docker compose up -d
```

Uygulamayı çalıştırın:

```bash
./mvnw spring-boot:run
```

Testleri çalıştırın:

```bash
./mvnw test
```

Production ortamında veritabanı ve sunucu ayarları environment variable olarak verilmelidir.
