## AKAI - Android Key Attestation Info

Akai is an utility application written for extracting properties of device's hardware-backed keys from attestation certificate's extension data.

Application initially was written mostly for learning and self-development purposes, however it can be used by developers/integrators for a quick verification of attestation properties send to a server from a device. 

Program is inspired by Google's server sample: https://github.com/google/android-key-attestation

Android documentation describing hardware-backed key pair verification: https://developer.android.com/training/articles/security-key-attestation


## Build

#### **Prerequisites:**

* `sbt` in version `1.5.5`
* `native-image`  for building GraalVM Native Image (https://www.graalvm.org/reference-manual/native-image/ )

#### **Compile:**
```bash
sbt clean compile
```

#### **Run all unit tests:**
```bash
sbt test
```

#### **Build uber-jar package:**
```bash
sbt assembly
```

Akai jar package can be found in:

```
target/scala-3.1.0
```

#### **Build GraalVM Native Image:**
```bash
sbt graalvm-native-image:packageBin
```
Akai GraalVM native image can be found in:
```
target/graalvm-native-image
```

## Install

Chose the convinient location (e.g. your own home directory) and copy there the build artifact (jar or native image). Add it to your system environment variable `PATH`, if it is not already there.


## Run

Akai reads certificate from the file or from the standard input. In case, when file location is not provided, standard input is a default source of data. 
Result with key description is printed to standard output.

Akai supports certificates in PEM and DER format.

Usage:
```
Usage: akai [OPTION]... [FILE]\n
With no FILE, read standard input.
 
Options
  --decode-base64           decode the input using Base64 decoding scheme 
                            before parsing as a X509 certificate 

  --json-format             format output as a JSON object

  --table-format            format output as a table (default option)

  --raw-values              do not translate schema values to human 
                            friendly format e.g. octet strings will 
                            be printed in hex format instead of 
                            converting to pritable characters

  --human-friendly-values   translate schema values to human friendly
                            format e.g. map integer to corresponding 
                            enum values (default option)

  --help                    display this help message and exit

```

Examples of usage can be found in the [dedicated document](EXAMPLES.md)