@echo off
echo ========================================
echo   Spring Boot 看板应用启动脚本
echo ========================================
echo.

echo 正在启动应用...
echo 访问地址: http://localhost:8081
echo H2控制台: http://localhost:8081/h2-console
echo.

mvn spring-boot:run

pause