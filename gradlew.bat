@echo off
setlocal

set "DIR=%~dp0"
set "WRAPPER_DIR=%DIR%gradle\wrapper"

set "LOCAL_GRADLE_HOME=%GRADLE_HOME%"
if "%LOCAL_GRADLE_HOME%"=="" if exist "%ProgramFiles%\gradle-8.14.3" set "LOCAL_GRADLE_HOME=%ProgramFiles%\gradle-8.14.3"
set "LOCAL_GRADLE_BIN=%LOCAL_GRADLE_HOME%\bin\gradle.bat"

if defined LOCAL_GRADLE_HOME if exist "%LOCAL_GRADLE_BIN%" (
  call "%LOCAL_GRADLE_BIN%" %*
  set "EXIT_CODE=%ERRORLEVEL%"
  endlocal & exit /b %EXIT_CODE%
)

if not "%JAVA_HOME%"=="" (
  set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVA_EXE="
  for /f "usebackq delims=" %%J in (`where java 2^>nul`) do (
    set "JAVA_EXE=%%~fJ"
    goto hasJava
  )
  echo ERROR: JAVA_HOME is not set and no 'java' executable could be found on the system PATH.>&2
  exit /b 1
)

goto hasJava

:hasJava
if "%JAVA_EXE%"=="" (
  echo ERROR: Unable to locate Java executable.>&2
  exit /b 1
)
if not exist "%JAVA_EXE%" (
  echo ERROR: Unable to locate Java executable %JAVA_EXE%.>&2
  exit /b 1
)

set "FALLBACK_WRAPPER=%WRAPPER_DIR%\gradle-wrapper.jar"
"%JAVA_EXE%" -classpath "%FALLBACK_WRAPPER%" org.gradle.wrapper.GradleWrapperMain %*
set "EXIT_CODE=%ERRORLEVEL%"
endlocal & exit /b %EXIT_CODE%
