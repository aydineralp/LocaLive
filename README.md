📍 LocaLive
LocaLive, kullanıcıların kültürel etkinlik tercihlerine göre (tiyatro, konser, stand-up vb.) gezi planları oluşturabildiği, lokasyon bazlı bir mobil uygulamadır. Kullanıcılar tercihlerine göre öneriler alabilir, konumlarını paylaşarak kişiselleştirilmiş etkinlik rotaları oluşturabilir.

🧩 Proje Yapısı 

                    LocaLive/
├── LocaLive-Backend/       # ASP.NET Core Web API (MySQL)
└── LocaLive-Frontend/      # Android (Kotlin) Mobil Uygulama

🚀 Özellikler
✅ Backend (ASP.NET Core)
RESTful API

Kullanıcı yönetimi (Register/Login)

Etkinlik öneri motoru (Ticketmaster & ChatGPT servisleri ile)

Entity Framework Core & MySQL

Swagger desteği

📱 Frontend (Android – Kotlin)
Kullanıcı kayıt & giriş ekranı

Kültürel tercihlere göre puanlama (Konser, Tiyatro, Stand-up)

Otomatik veya manuel konum seçimi

Etkinliklerin webview ve card ile gösterimi

Profil ekranı ve gezi geçmişi görüntüleme

Konum bazlı önerilen etkinlikleri haritada gösterme (map.html)


| Katman   | Teknoloji                             |
| -------- | ------------------------------------- |
| Backend  | ASP.NET Core 8, MySQL, EF Core        |
| Frontend | Android, Kotlin, Retrofit             |
| Diğer    | Swagger, Ticketmaster API, OpenAI GPT |


⚙️ Kurulum
cd LocaLive-Backend/LocaLive
dotnet restore
dotnet run

appsettings.json dosyasındaki AppDbConnectionString değerini kendi MySQL bilginizle değiştirin.

Android (Frontend)
Android Studio ile LocaLive-Frontend/LocaLive klasörünü açın.

Gerekli izinleri (INTERNET, ACCESS_FINE_LOCATION vb.) AndroidManifest.xml içinde kontrol edin.

Cihazda ya da emülatörde çalıştırın.

⚠️ ApiClient.kt içinde base URL'yi backend adresinize göre güncelleyin.

![image](https://github.com/user-attachments/assets/3bed9aa4-9ff1-4896-9e49-2d95143556e4)

![image](https://github.com/user-attachments/assets/97c391be-0841-42cf-9eee-3780921098ca)

![image](https://github.com/user-attachments/assets/6aeb6cea-c3fe-49d3-b90a-68f8293542f6)

![image](https://github.com/user-attachments/assets/3faaac8d-98d4-42c0-ac62-a7a752bf180c)

![image](https://github.com/user-attachments/assets/45fc987e-3b07-4ee6-b96c-3e28aa5cd23d)

