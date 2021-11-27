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
attestationChallenge                 | 61 62 63                             |                                     
attestationSecurityLevel             | TrustedEnvironment                   |                                     
attestationVersion                   | 3                                    |                                     
keymasterSecurityLevel               | TrustedEnvironment                   |                                     
keymasterVersion                     | 4                                    |                                     
uniqueId                             |                                      |                                     
------------------------------------ | ------------------------------------ | ------------------------------------
Authorization list                   | Software enforced                    | TEE enforced                        
------------------------------------ | ------------------------------------ | ------------------------------------
activeDateTime                       |                                      |                                     
algorithm                            |                                      | RSA                                 
allApplications                      | false                                | false                               
allowWhileOnBody                     | false                                | false                               
applicationId                        |                                      |                                     
attestationApplicationId             | 0...1...0...android...0...com.androi |                                     
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
attestationIdBrand                   |                                      |                                     
attestationIdDevice                  |                                      |                                     
attestationIdImei                    |                                      |                                     
attestationIdManufacturer            |                                      |                                     
attestationIdMeid                    |                                      |                                     
attestationIdModel                   |                                      |                                     
attestationIdProduct                 |                                      |                                     
attestationIdSerial                  |                                      |                                     
authTimeout                          |                                      |                                     
bootPatchLevel                       |                                      | 201907                              
creationDateTime                     | 2018-07-29T12:31:54.759Z             |                                     
digest                               |                                      | SHA-2-256                           
ecCurve                              |                                      |                                     
keySize                              |                                      | 2048                                
noAuthRequired                       | false                                | true                                
origin                               |                                      | Generated                           
originationExpireDateTime            |                                      |                                     
osPatchLevel                         |                                      | 201907                              
osVersion                            |                                      | 0                                   
padding                              |                                      | RSA-PKCS1-1-5-SIGN, RSA-PSS         
purpose                              |                                      | Verify, Sign                        
rollbackResistance                   | false                                | false                               
rootOfTrust.deviceLocked             |                                      | false                               
rootOfTrust.verifiedBootHash         |                                      | 72 8d b1 27 4f 1f 1c f1 57 1d e4 38 
                                     |                                      | 0b 04 8a 55 4a c4 a3 80 e7 6f 53 55 
                                     |                                      | 08 35 29 08 4a 93 78 01             
rootOfTrust.verifiedBootKey          |                                      | 00 00 00 00 00 00 00 00 00 00 00 00 
                                     |                                      | 00 00 00 00 00 00 00 00 00 00 00 00 
                                     |                                      | 00 00 00 00 00 00 00 00             
rootOfTrust.verifiedBootState        |                                      | Unverified                          
rsaPublicExponent                    |                                      | 65537                               
trustedConfirmationRequired          | false                                | false                               
trustedUserPresenceRequired          | false                                | false                               
unlockedDeviceRequired               | false                                | false                               
usageExpireDateTime                  |                                      |                                     
userAuthType                         |                                      |                                     
vendorPatchLevel                     |                                      | 201907                                                 

```

### **2. Parsing certificate from file/standard and output in _JSON_ format**

Passing certificate from standard input:
``` bash
cat ~/cert0.pem | akai --json-format
```

will print to standard output the key description in the JSON format:
```
{"attestationChallenge":"616263","attestationSecurityLevel":"TrustedEnvironment","attestationVersion":3,"keymasterSecurityLevel":"TrustedEnvironment","keymasterVersion":4,"softwareEnforced":{"activeDateTime":null,"algorithm":null,"allApplications":false,"allowWhileOnBody":false,"applicationId":null,"attestationApplicationId":"0...1...0...android...0...com.android.keychain...0...com.android.settings...0...com.qti.diagservices...0...com.android.dynsystem...0...com.android.inputdevices...0...com.android.localtransport...0...com.android.location.fused...0...com.android.server.telecom...0 ..com.android.wallpaperbackup...0!..com.google.SSRestartDetector...0\"..com.google.android.hiddenmenu...0#..com.android.providers.settings...1\". 0.....4P.E.B*.f.B$.]._......o.f.","attestationIdBrand":"","attestationIdDevice":"","attestationIdImei":"","attestationIdManufacturer":"","attestationIdMeid":"","attestationIdModel":"","attestationIdProduct":"","attestationIdSerial":"","authTimeout":null,"bootPatchLevel":null,"creationDateTime":"2018-07-29T12:31:54.759Z","digest":[],"ecCurve":[],"keySize":null,"noAuthRequired":false,"origin":null,"originationExpireDateTime":null,"osPatchLevel":null,"osVersion":null,"padding":[],"purpose":[],"rollbackResistance":false,"rootOfTrust":{"deviceLocked":null,"verifiedBootHash":null,"verifiedBootKey":null,"verifiedBootState":null},"rsaPublicExponent":null,"trustedConfirmationRequired":false,"trustedUserPresenceRequired":false,"unlockedDeviceRequired":false,"usageExpireDateTime":null,"userAuthType":null,"vendorPatchLevel":null},"teeEnforced":{"activeDateTime":null,"algorithm":"RSA","allApplications":false,"allowWhileOnBody":false,"applicationId":null,"attestationApplicationId":"","attestationIdBrand":"","attestationIdDevice":"","attestationIdImei":"","attestationIdManufacturer":"","attestationIdMeid":"","attestationIdModel":"","attestationIdProduct":"","attestationIdSerial":"","authTimeout":null,"bootPatchLevel":201907,"creationDateTime":null,"digest":["SHA-2-256"],"ecCurve":[],"keySize":2048,"noAuthRequired":true,"origin":"Generated","originationExpireDateTime":null,"osPatchLevel":201907,"osVersion":0,"padding":["RSA-PKCS1-1-5-SIGN", "RSA-PSS"],"purpose":["Verify", "Sign"],"rollbackResistance":false,"rootOfTrust":{"deviceLocked":false,"verifiedBootHash":"728db1274f1f1cf1571de4380b048a554ac4a380e76f5355083529084a937801","verifiedBootKey":"0000000000000000000000000000000000000000000000000000000000000000","verifiedBootState":"Unverified"},"rsaPublicExponent":65537,"trustedConfirmationRequired":false,"trustedUserPresenceRequired":false,"unlockedDeviceRequired":false,"usageExpireDateTime":null,"userAuthType":null,"vendorPatchLevel":201907},"uniqueId":null}
```

To print key description in the pretty JSON format it can be piped to the [`jq`](https://stedolan.github.io/jq/download/)

``` bash
cat ~/cert0.pem | akai --json-format | jq
```

Exemplary result:

```
{
  "attestationChallenge": "616263",
  "attestationSecurityLevel": "TrustedEnvironment",
  "attestationVersion": 3,
  "keymasterSecurityLevel": "TrustedEnvironment",
  "keymasterVersion": 4,
  "softwareEnforced": {
    "activeDateTime": null,
    "algorithm": null,
    "allApplications": false,
    "allowWhileOnBody": false,
    "applicationId": null,
    "attestationApplicationId": "0...1...0...android...0...com.android.keychain...0...com.android.settings...0...com.qti.diagservices...0...com.android.dynsystem...0...com.android.inputdevices...0...com.android.localtransport...0...com.android.location.fused...0...com.android.server.telecom...0 ..com.android.wallpaperbackup...0!..com.google.SSRestartDetector...0\"..com.google.android.hiddenmenu...0#..com.android.providers.settings...1\". 0.....4P.E.B*.f.B$.]._......o.f.",
    "attestationIdBrand": "",
    "attestationIdDevice": "",
    "attestationIdImei": "",
    "attestationIdManufacturer": "",
    "attestationIdMeid": "",
    "attestationIdModel": "",
    "attestationIdProduct": "",
    "attestationIdSerial": "",
    "authTimeout": null,
    "bootPatchLevel": null,
    "creationDateTime": "2018-07-29T12:31:54.759Z",
    "digest": [],
    "ecCurve": [],
    "keySize": null,
    "noAuthRequired": false,
    "origin": null,
    "originationExpireDateTime": null,
    "osPatchLevel": null,
    "osVersion": null,
    "padding": [],
    "purpose": [],
    "rollbackResistance": false,
    "rootOfTrust": {
      "deviceLocked": null,
      "verifiedBootHash": null,
      "verifiedBootKey": null,
      "verifiedBootState": null
    },
    "rsaPublicExponent": null,
    "trustedConfirmationRequired": false,
    "trustedUserPresenceRequired": false,
    "unlockedDeviceRequired": false,
    "usageExpireDateTime": null,
    "userAuthType": null,
    "vendorPatchLevel": null
  },
  "teeEnforced": {
    "activeDateTime": null,
    "algorithm": "RSA",
    "allApplications": false,
    "allowWhileOnBody": false,
    "applicationId": null,
    "attestationApplicationId": "",
    "attestationIdBrand": "",
    "attestationIdDevice": "",
    "attestationIdImei": "",
    "attestationIdManufacturer": "",
    "attestationIdMeid": "",
    "attestationIdModel": "",
    "attestationIdProduct": "",
    "attestationIdSerial": "",
    "authTimeout": null,
    "bootPatchLevel": 201907,
    "creationDateTime": null,
    "digest": [
      "SHA-2-256"
    ],
    "ecCurve": [],
    "keySize": 2048,
    "noAuthRequired": true,
    "origin": "Generated",
    "originationExpireDateTime": null,
    "osPatchLevel": 201907,
    "osVersion": 0,
    "padding": [
      "RSA-PKCS1-1-5-SIGN",
      "RSA-PSS"
    ],
    "purpose": [
      "Verify",
      "Sign"
    ],
    "rollbackResistance": false,
    "rootOfTrust": {
      "deviceLocked": false,
      "verifiedBootHash": "728db1274f1f1cf1571de4380b048a554ac4a380e76f5355083529084a937801",
      "verifiedBootKey": "0000000000000000000000000000000000000000000000000000000000000000",
      "verifiedBootState": "Unverified"
    },
    "rsaPublicExponent": 65537,
    "trustedConfirmationRequired": false,
    "trustedUserPresenceRequired": false,
    "unlockedDeviceRequired": false,
    "usageExpireDateTime": null,
    "userAuthType": null,
    "vendorPatchLevel": 201907
  },
  "uniqueId": null
}

```