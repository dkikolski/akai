## AKAI - Android Key Attestation Info
---

Akai is an utility application written for extracting properties of device's hardware-backed keys from attestation certificate's extension data.

Application initially was written mostly for learning and self-development purposes, however it can be used by developers/integrators for a quick verification of attestation properties send to a server from a device. 

Program is inspired by Google's server sample: https://github.com/google/android-key-attestation

Android documentation describing hardware-backed key pair verification: https://developer.android.com/training/articles/security-key-attestation

<br />

## Build
---

#### Prerequisites

* `sbt` in version `1.5.5`
* `native-image`  for building GraalVM Native Image (https://www.graalvm.org/reference-manual/native-image/ )

#### Compile:
```bash
sbt clean compile
```

#### Run all unit tests:
```bash
sbt test
```

#### Build uber-jar package:
```bash
sbt assembly
```

#### Build GraalVM Native Image:
```bash
sbt graalvm-native-image:packageBin
```

<br />

## Install
---
Chose the convinient location (e.g. your own home directory) and copy there the build artifact (jar or native image). Add it to your system environment variable `PATH`, if it is not already there.


<br />

## Run
---
Akai reads certificate from the file or from the standard input. In case, when file location is not provided, standard input is a default source of data. 
Result with key description is printed to standard output.

Akai supports certificates in PEM and DER format.

Following examples assumes that native image was build, installed and it is available in the `PATH`. The same can be achieved with application packaged to `jar` file. In such case `java -jar akai` should be used instead. 

As a exemplary input data, certificate from the Google's demo will be used (e.g. https://github.com/google/android-key-attestation/blob/master/server/examples/pem/algorithm_RSA_SecurityLevel_TEE/cert0.pem)

**Example:**

Using file as an input:
```bash
akai ~/cert0.pem
```

or passing certificate from stdin:
``` bash
cat ~/cert0.pem | akai
```

will result the following output with key description:

```
------------------------------------ | ------------------------------------ | ------------------------------------
Attestation info                     |                                      |                                     
------------------------------------ | ------------------------------------ | ------------------------------------
Attestation Version                  | 3                                    |                                     
Attestation Security Level           | TrustedEnvironment                   |                                     
Keymaster Version                    | 4                                    |                                     
Keymaster Security Level             | TrustedEnvironment                   |                                     
Attestation Challenge                | 61 62 63                             |                                     
Unique ID                            | <empty>                              |                                     
------------------------------------ | ------------------------------------ | ------------------------------------
Authorization list                   | Software enforced                    | TEE enforced                        
------------------------------------ | ------------------------------------ | ------------------------------------
Purpose                              | <empty>                              | Verify, Sign                        
Algorithm                            | <empty>                              | RSA                                 
Key Size                             | <empty>                              | 2048                                
Digest                               | <empty>                              | SHA-2-256                           
Padding                              | <empty>                              | RSA-PKCS1-1-5-SIGN, RSA-PSS         
EC Curve                             | <empty>                              | <empty>                             
RSA public Exponent                  | <empty>                              | 65537                               
Rollback Resistance                  | false                                | false                               
Active Date Time                     | <empty>                              | <empty>                             
Origination Expire Date Time         | <empty>                              | <empty>                             
Usage Expire Date Time               | <empty>                              | <empty>                             
No Auth Required                     | false                                | true                                
User Auth Type                       | <empty>                              | <empty>                             
Auth Timeout                         | <empty>                              | <empty>                             
Allow While On Body                  | false                                | false                               
Trusted User Presence Required       | false                                | false                               
Trusted Confirmation Required        | false                                | false                               
Unlocked Device Required             | false                                | false                               
All Applications                     | false                                | false                               
Application ID                       | <empty>                              | <empty>                             
Creation Date Time                   | 2018-07-29T12:31:54.759Z             | <empty>                             
Origin                               | <empty>                              | Generated                           
Root of Trust: Verified Boot Key     | <empty>                              | 00 00 00 00 00 00 00 00 00 00 00 00 
                                     |                                      | 00 00 00 00 00 00 00 00 00 00 00 00 
                                     |                                      | 00 00 00 00 00 00 00 00             
Root of Trust: Device Locked         | <empty>                              | false                               
Root of Trust: Verified Boot State   | <empty>                              | Unverified                          
Root of Trust: Verified Boot Hash    | <empty>                              | <empty>                             
OS Version                           | <empty>                              | 0                                   
OS Patch Level                       | <empty>                              | 201907                              
Attestation Application ID           | 0...1...0...android...0...com.androi | <empty>                             
                                     | d.keychain...0...com.android.setting |                                     
                                     | s...0...com.qti.diagservices...0...c |                                     
                                     | om.android.dynsystem...0...com.andro |                                     
                                     | id.inputdevices...0...com.android.lo |                                     
                                     | caltransport...0...com.android.locat |                                     
                                     | ion.fused...0...com.android.server.t |                                     
                                     | elecom...0 ..com.android.wallpaperba |                                     
                                     | ckup...0!..com.google.SSRestartDetec |                                     
                                     | tor...0"..com.google.android.hiddenm |                                     
                                     | enu...0#..com.android.providers.sett |                                     
                                     | ings...1". 0.....4P.E.B*.f.B$.]._... |                                     
                                     | ...o.f.                              |                                     
Attestation ID Brand                 | <empty>                              | <empty>                             
Attestation ID Device                | <empty>                              | <empty>                             
Attestation ID Product               | <empty>                              | <empty>                             
Attestation ID Serial                | <empty>                              | <empty>                             
Attestation ID IMEI                  | <empty>                              | <empty>                             
Attestation ID MEID                  | <empty>                              | <empty>                             
Attestation ID Manufacturer          | <empty>                              | <empty>                             
Attestation ID Model                 | <empty>                              | <empty>                             
Vendor Patch level                   | <empty>                              | 201907                              
Boot patch level                     | <empty>                              | 201907
```

