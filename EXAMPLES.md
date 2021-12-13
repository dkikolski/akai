## General info

Following examples assumes that native image was build, installed and it is available in the `PATH`. The same can be achieved with application packaged to `jar` file. In such case `java -jar akai` should be used instead. 

As an exemplary input data, certificates from the Google's demo will be used (https://github.com/google/android-key-attestation/blob/master/server/examples/pem/algorithm_RSA_SecurityLevel_TEE/cert0.pem)


## Examples

### **1. Parsing certificate from file/standard input and output in _table_ format**

Using file as an input:
```bash
akai ~/cert0.pem
```

or passing certificate from standard input, for instance using `cat`:
``` bash
cat ~/cert0.pem | akai
```

will print to standard output the following table with key description:

```
------------------------------------ | ------------------------------------ | ------------------------------------
Attestation info                     |                                      |                                     
------------------------------------ | ------------------------------------ | ------------------------------------
Attestation version                  | 3                                    |                                     
Attestation Security Level           | TrustedEnvironment                   |                                     
Keymaster Version                    | 4                                    |                                     
Keymaster Security Level             | TrustedEnvironment                   |                                     
Attestation Challenge                | 61 62 63                             |                                     
Unique ID                            |                                      |                                     
------------------------------------ | ------------------------------------ | ------------------------------------
Authorization list                   | Software enforced                    | TEE enforced                        
------------------------------------ | ------------------------------------ | ------------------------------------
Purpose                              |                                      | Verify, Sign                        
Algorithm                            |                                      | RSA                                 
Key Size                             |                                      | 2048                                
Digest                               |                                      | SHA-2-256                           
Padding                              |                                      | RSA-PKCS1-1-5-SIGN, RSA-PSS         
EC Curve                             |                                      |                                     
RSA Public Exponent                  |                                      | 65537                               
Rollback Resistance                  | false                                | false                               
Active DateTime                      |                                      |                                     
Origination Expire DateTime          |                                      |                                     
Usage Expire DateTime                |                                      |                                     
NoAuth Required                      | false                                | true                                
User Auth Type                       |                                      |                                     
Auth Timeout                         |                                      |                                     
Allow While On Body                  | false                                | false                               
Trusted User Presence Required       | false                                | false                               
Trusted Confirmation Required        | false                                | false                               
Unlocked Device Required             | false                                | false                               
All Applications                     | false                                | false                               
Application Id                       |                                      |                                     
Creation DateTime                    | 2018-07-29T12:31:54.759Z             |                                     
Origin                               |                                      | Generated                           
OS Version                           |                                      | 0                                   
OS Patch Level                       |                                      | 201907                              
Attestation ID Brand                 |                                      |                                     
Attestation ID Device                |                                      |                                     
Attestation ID Product               |                                      |                                     
Attestation ID Serial                |                                      |                                     
Attestation ID Imei                  |                                      |                                     
Attestation ID Meid                  |                                      |                                     
Attestation ID Manufacturer          |                                      |                                     
Attestation ID Model                 |                                      |                                     
Vendor Patch Level                   |                                      | 201907                              
Boot Patch Level                     |                                      | 201907                              
------------------------------------ | ------------------------------------ | ------------------------------------
Root of Trust                        | Software enforced                    | TEE enforced                        
------------------------------------ | ------------------------------------ | ------------------------------------
Verified Boot Key                    |                                      | 00 00 00 00 00 00 00 00 00 00 00 00 
                                     |                                      | 00 00 00 00 00 00 00 00 00 00 00 00 
                                     |                                      | 00 00 00 00 00 00 00 00             
Device Locked                        |                                      | false                               
Verified Boot State                  |                                      | Unverified                          
Verified Boot Hash                   |                                      | 72 8d b1 27 4f 1f 1c f1 57 1d e4 38 
                                     |                                      | 0b 04 8a 55 4a c4 a3 80 e7 6f 53 55 
                                     |                                      | 08 35 29 08 4a 93 78 01             
------------------------------------ | ------------------------------------ | ------------------------------------
Attestation App ID                   | Software Enforced                    | TEE enforced                        
------------------------------------ | ------------------------------------ | ------------------------------------
Package Infos (name: version)        | ('com.android.wallpaperbackup': 29), |                                     
                                     | ('com.android.dynsystem': 29), ('com |                                     
                                     | .google.SSRestartDetector': 29), ('c |                                     
                                     | om.google.android.hiddenmenu': 1), ( |                                     
                                     | 'com.qti.diagservices': 29), ('andro |                                     
                                     | id': 29), ('com.android.keychain': 2 |                                     
                                     | 9), ('com.android.providers.settings |                                     
                                     | ': 29), ('com.android.localtransport |                                     
                                     | ': 29), ('com.android.server.telecom |                                     
                                     | ': 29), ('com.android.inputdevices': |                                     
                                     | 29), ('com.android.location.fused':  |                                     
                                     | 29), ('com.android.settings': 29)    |                                     
Digests                              | 30 1a a3 cb 08 11 34 50 1c 45 f1 42  |                                     
                                     | 2a bc 66 c2 42 24 fd 5d ed 5f dc 8f  |                                     
                                     | 17 e6 97 17 6f d8 66 aa              |                                     

```
