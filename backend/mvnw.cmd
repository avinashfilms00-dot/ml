@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF)
@REM Maven Wrapper startup batch script for Windows
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0teleetype'; $env:__MVNW_CMD__=Join-Path $scriptDir.Substring(0,$scriptDir.Length-9) '.mvn\wrapper\MavenWrapperDownloader.class'; exit}" 2^>NUL`) DO @(
  IF /I "%%A"=="__MVNW_CMD__" SET __MVNW_CMD__=%%B
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=

@SET WRAPPER_JAR="%~dp0.mvn\wrapper\maven-wrapper.jar"
@SET WRAPPER_PROPERTIES="%~dp0.mvn\wrapper\maven-wrapper.properties"

@IF NOT EXIST %WRAPPER_JAR% (
  @ECHO Maven Wrapper JAR not found. Downloading...
  @powershell -Command "& {Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '%~dp0.mvn\wrapper\maven-wrapper.jar' -UseBasicParsing}"
)

@SET MAVEN_PROJECTBASEDIR=%~dp0
@SET MAVEN_CMD_LINE_ARGS=%*

@FOR /F "usebackq tokens=1,2 delims==" %%A IN (%WRAPPER_PROPERTIES%) DO @(
  IF "%%A"=="distributionUrl" SET MVNW_REPOURL=%%B
)

"%JAVA_HOME%\bin\java.exe" ^
  -classpath %WRAPPER_JAR% ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  org.apache.maven.wrapper.MavenWrapperMain ^
  %MAVEN_CMD_LINE_ARGS%

IF ERRORLEVEL 1 GOTO error
GOTO end

:error
@ECHO.
@ECHO ERROR: Maven wrapper failed.
EXIT /B 1

:end
