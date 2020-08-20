#!/bin/bash -x

SERVER=${1:-$(hostname)}
CONF=openssl.conf
cat > $CONF <<EOF
[req]
distinguished_name=distinguished_name
[distinguished_name]

[v3_ca]
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid:always,issuer
basicConstraints = critical, CA:true
keyUsage = critical, digitalSignature, cRLSign, keyCertSign

[v3_ca_0]
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid:always,issuer
basicConstraints = critical, CA:true, pathlen:0
keyUsage = critical, digitalSignature, cRLSign, keyCertSign

[v3_req]
subjectKeyIdentifier = hash
basicConstraints = critical, CA:true
keyUsage = critical, digitalSignature, cRLSign, keyCertSign

[v3_req_0]
subjectKeyIdentifier = hash
basicConstraints = critical, CA:true, pathlen:0
keyUsage = critical, digitalSignature, cRLSign, keyCertSign

[server]
basicConstraints = critical, CA:false
keyUsage = critical, keyEncipherment, dataEncipherment
extendedKeyUsage = critical, serverAuth
subjectAltName = DNS:$SERVER

[v3_not_ca]
basicConstraints = critical, CA:false

[v3_ca_wrong_keyUsage]
keyUsage = critical, keyEncipherment, dataEncipherment

[v3_ca_wrong_extendedKeyUsage]
extendedKeyUsage = critical, serverAuth
EOF

SERIAL=serial.txt
echo 1000 > $SERIAL
PASS=pass:password

# Create a server csr which will be used in multiple places
openssl req -new -keyout server.key -out server.csr -subj "/C=IN/ST=Karnataka/L=Bengaluru/O=VMware/OU=CPBU/CN=$SERVER" -passout $PASS -config $CONF -reqexts server

########################################################################################################################

# Create a self signed root CA which can sign intermediate certificates
openssl req -new -x509 -keyout rootca.key -out rootca.crt -subj '/CN=Root CA' -passout $PASS -config $CONF -extensions v3_ca

# Create a intermediate certificate which cannot sign any other intermediate certificates
openssl req -new -keyout intermediate0.key -out intermediate0.csr -subj '/CN=Intermediate 0' -passout $PASS -config $CONF -reqexts v3_req_0
openssl x509 -req -sha256 -CA rootca.crt -CAkey rootca.key -passin $PASS -CAserial $SERIAL -in intermediate0.csr -out intermediate0.crt -extfile $CONF -extensions v3_ca_0

# Sign a server certificate by the valid intermediate certificate
openssl x509 -req -sha256 -CA intermediate0.crt -CAkey intermediate0.key -passin $PASS -CAserial $SERIAL -in server.csr -out server.crt -extfile $CONF -extensions server

# Verify a valid certificate chain
cat intermediate0.crt rootca.crt > intermediate0-rootca.crt
openssl verify -issuer_checks -policy_check -x509_strict -CAfile intermediate0-rootca.crt server.crt

# Verify a certificate chain in the wrong order
cat rootca.crt intermediate0.crt > rootca-intermediate0.crt
openssl verify -issuer_checks -policy_check -x509_strict -CAfile rootca-intermediate0.crt server.crt

# Verify a certificate with missing root CA
openssl verify -issuer_checks -policy_check -x509_strict -CAfile intermediate0.crt server.crt

# Verify a certificate with missing intermediate CA
openssl verify -issuer_checks -policy_check -x509_strict -CAfile rootca.crt server.crt

########################################################################################################################

# Create a self signed root CA which cannot sign intermediate certificates
openssl req -new -x509 -keyout rootca0.key -out rootca0.crt -subj '/CN=Root CA 0' -passout $PASS -config $CONF -extensions v3_ca_0

# Create a intermediate certificate which is signed by the above root certificate
openssl req -new -keyout intermediate.key -out intermediate.csr -subj '/CN=Intermediate' -passout $PASS -config $CONF -reqexts v3_req
openssl x509 -req -sha256 -CA rootca0.crt -CAkey rootca0.key -passin $PASS -CAserial $SERIAL -in intermediate.csr -out intermediate.crt -extfile $CONF -extensions v3_ca

# Sign a server certificate by the above intermediate certificate
openssl x509 -req -sha256 -CA intermediate.crt -CAkey intermediate.key -passin $PASS -CAserial $SERIAL -in server.csr -out server0.crt -extfile $CONF -extensions server

# Verify a certificate with wrong CA path length
cat intermediate.crt rootca0.crt > intermediate-rootca0.crt
openssl verify -issuer_checks -policy_check -x509_strict -CAfile intermediate-rootca0.crt server0.crt

########################################################################################################################

# Create a self signed CA with no extension
openssl req -new -x509 -keyout rootca-no-ext.key -out rootca-no-ext.crt -subj '/CN=Root CA No Extension' -passout $PASS -config $CONF

# Sign a server certificate by the root CA having no extension
openssl x509 -req -sha256 -CA rootca-no-ext.crt -CAkey rootca-no-ext.key -passin $PASS -CAserial $SERIAL -in server.csr -out server-no-issuer-ext.crt -extfile $CONF -extensions server

# Verify a certificate with no extension
openssl verify -issuer_checks -policy_check -x509_strict -CAfile rootca-no-ext.crt server-no-issuer-ext.crt

########################################################################################################################

# Create a self signed CA with CA:false
openssl req -new -x509 -keyout not-ca.key -out not-ca.crt -subj '/CN=Not CA' -passout $PASS -config $CONF -extensions v3_not_ca

# Sign a server certificate by the invalid issuer CA
openssl x509 -req -sha256 -CA not-ca.crt -CAkey not-ca.key -passin $PASS -CAserial $SERIAL -in server.csr -out server-issuer-not-ca.crt -extfile $CONF -extensions server

# Verify a certificate with invalid issuer CA
openssl verify -issuer_checks -policy_check -x509_strict -CAfile not-ca.crt server-issuer-not-ca.crt

########################################################################################################################

# Create a self signed CA with wrong keyUsage
openssl req -new -x509 -keyout rootca-wrong-keyUsage.key -out rootca-wrong-keyUsage.crt -subj '/CN=Root CA wrong keyUsage' -passout $PASS -config $CONF -extensions v3_ca_wrong_keyUsage

# Sign a server certificate by the invalid issuer keyUsage
openssl x509 -req -sha256 -CA rootca-wrong-keyUsage.crt -CAkey rootca-wrong-keyUsage.key -passin $PASS -CAserial $SERIAL -in server.csr -out wrong-issuer-keyUsage.crt -extfile $CONF -extensions server

# Verify a certificate with invalid keyUsage
openssl verify -issuer_checks -policy_check -x509_strict -CAfile rootca-wrong-keyUsage.crt wrong-issuer-keyUsage.crt

########################################################################################################################

# Create a self signed CA with wrong extendedKeyUsage
openssl req -new -x509 -keyout rootca-wrong-extendedKeyUsage.key -out rootca-wrong-extendedKeyUsage.crt -subj '/CN=Root CA wrong extendedKeyUsage' -passout $PASS -config $CONF -extensions v3_ca_wrong_extendedKeyUsage

# Sign a server certificate by the invalid issuer extendedKeyUsage
openssl x509 -req -sha256 -CA rootca-wrong-extendedKeyUsage.crt -CAkey rootca-wrong-extendedKeyUsage.key -passin $PASS -CAserial $SERIAL -in server.csr -out wrong-issuer-extendedKeyUsage.crt -extfile $CONF -extensions server

# Verify a certificate with invalid extendedKeyUsage
openssl verify -issuer_checks -policy_check -x509_strict -CAfile rootca-wrong-extendedKeyUsage.crt wrong-issuer-extendedKeyUsage.crt

