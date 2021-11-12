## General info

Following examples assumes that native image was build, installed and it is available in the `PATH`. The same can be achieved with application packaged to `jar` file. In such case `java -jar akai` should be used instead. 

As an exemplary input data, certificates from the Google's demo will be used (https://github.com/google/android-key-attestation/blob/master/server/examples/pem/algorithm_RSA_SecurityLevel_TEE/cert0.pem)


## Examples

**Example:** Parsing certificate from file/standard input

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
Root of Trust: Verified Boot Hash    | <empty>                              | 72 8d b1 27 4f 1f 1c f1 57 1d e4 38 
                                     |                                      | 0b 04 8a 55 4a c4 a3 80 e7 6f 53 55 
                                     |                                      | 08 35 29 08 4a 93 78 01             
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

