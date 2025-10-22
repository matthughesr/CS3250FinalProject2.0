@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  FinalProject2.0 startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and FINAL_PROJECT2_0_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\FinalProject2.0.jar;%APP_HOME%\lib\client-java-19.0.0.jar;%APP_HOME%\lib\slf4j-simple-2.0.9.jar;%APP_HOME%\lib\javafx-fxml-17.0.2-win.jar;%APP_HOME%\lib\javafx-controls-17.0.2-win.jar;%APP_HOME%\lib\simpleclient_httpserver-0.16.0.jar;%APP_HOME%\lib\simpleclient_common-0.16.0.jar;%APP_HOME%\lib\simpleclient-0.16.0.jar;%APP_HOME%\lib\client-java-api-19.0.0.jar;%APP_HOME%\lib\client-java-proto-19.0.0.jar;%APP_HOME%\lib\snakeyaml-2.2.jar;%APP_HOME%\lib\commons-codec-1.16.0.jar;%APP_HOME%\lib\commons-compress-1.24.0.jar;%APP_HOME%\lib\commons-lang3-3.13.0.jar;%APP_HOME%\lib\commons-io-2.14.0.jar;%APP_HOME%\lib\jose4j-0.9.3.jar;%APP_HOME%\lib\slf4j-api-2.0.9.jar;%APP_HOME%\lib\bcpkix-jdk18on-1.76.jar;%APP_HOME%\lib\protobuf-java-3.24.3.jar;%APP_HOME%\lib\commons-collections4-4.4.jar;%APP_HOME%\lib\javafx-graphics-17.0.2-win.jar;%APP_HOME%\lib\simpleclient_tracer_otel-0.16.0.jar;%APP_HOME%\lib\simpleclient_tracer_otel_agent-0.16.0.jar;%APP_HOME%\lib\javax.annotation-api-1.3.2.jar;%APP_HOME%\lib\swagger-annotations-1.6.11.jar;%APP_HOME%\lib\logging-interceptor-4.11.0.jar;%APP_HOME%\lib\okhttp-4.11.0.jar;%APP_HOME%\lib\gson-fire-1.8.5.jar;%APP_HOME%\lib\gson-2.10.1.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\bcutil-jdk18on-1.76.jar;%APP_HOME%\lib\bcprov-jdk18on-1.76.jar;%APP_HOME%\lib\javafx-base-17.0.2-win.jar;%APP_HOME%\lib\simpleclient_tracer_common-0.16.0.jar;%APP_HOME%\lib\okio-jvm-3.2.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.6.20.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.6.20.jar;%APP_HOME%\lib\kotlin-stdlib-1.6.20.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.6.20.jar;%APP_HOME%\lib\annotations-13.0.jar


@rem Execute FinalProject2.0
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %FINAL_PROJECT2_0_OPTS%  -classpath "%CLASSPATH%" Main %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable FINAL_PROJECT2_0_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%FINAL_PROJECT2_0_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
