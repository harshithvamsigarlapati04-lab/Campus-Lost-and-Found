@echo off
@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper Startup Batch Script for Windows
@REM ----------------------------------------------------------------------------

setlocal

set ERROR_CODE=0
set "DIRNAME=%~dp0"
if "%DIRNAME%" == "" set "DIRNAME=."
set "APP_BASE_NAME=%~nx0"
set "APP_HOME=%DIRNAME%"

@REM Resolve Java executable
if not "%JAVA_HOME%" == "" goto OkJHome
for %%i in (java.exe) do set "JAVACMD=%%~$PATH:i"
if not "%JAVACMD%" == "" goto checkWrapper

:OkJHome
set "JAVACMD=%JAVA_HOME%\bin\java.exe"
if exist "%JAVACMD%" goto checkWrapper

echo [ERROR] JAVA_HOME is not set or invalid. Please check Java installation. >&2
goto error

:checkWrapper
set "WRAPPER_JAR=%APP_HOME%\.mvn\wrapper\maven-wrapper.jar"
set "WRAPPER_PROPS=%APP_HOME%\.mvn\wrapper\maven-wrapper.properties"

if exist "%WRAPPER_JAR%" goto runWrapper

if not exist "%APP_HOME%\.mvn\wrapper" mkdir "%APP_HOME%\.mvn\wrapper"

echo [INFO] Downloading Maven Wrapper jar...
set "WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

powershell -NoProfile -ExecutionPolicy Bypass -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%')"
if ERRORLEVEL 1 (
    echo [ERROR] Failed to download maven-wrapper.jar >&2
    goto error
)

:runWrapper
"%JAVACMD%" %MAVEN_OPTS% -Dmaven.multiModuleProjectDirectory=%APP_HOME% -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & exit /B %ERROR_CODE%
