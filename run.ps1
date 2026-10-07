Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "          CareerShield AI - Fake Internship & Job Detection" -ForegroundColor Yellow
Write-Host "                   'Verify Before You Apply.'" -ForegroundColor Green
Write-Host "======================================================================" -ForegroundColor Cyan

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
$env:M2_HOME = "C:\Users\sujit\.maven\apache-maven-3.9.6"
$env:Path = "$env:JAVA_HOME\bin;$env:M2_HOME\bin;" + [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

Write-Host "`nStarting CareerShield AI Spring Boot Server on http://localhost:8080 ...`n" -ForegroundColor Green
mvn spring-boot:run -o
