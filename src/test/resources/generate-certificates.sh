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
EOF

SERIAL=serial.txt
echo 1000 > $SERIAL
PASS=pass:password

# Create a server csr which will be used in multiple places
openssl req -new -keyout server.key -out server.csr -subj "/C=IN/ST=Karnataka/L=Bengaluru/O=VMware/OU=CPBU/CN=$SERVER" -passout $PASS -config $CONF -reqexts server

# Create Root -> Intermediate -> Server certificates
openssl req -new -x509 -keyout rootca.key -out rootca.crt -subj '/CN=Root CA' -passout $PASS -config $CONF -extensions v3_ca
openssl req -new -keyout intermediate0.key -out intermediate0.csr -subj '/CN=Intermediate 0' -passout $PASS -config $CONF -reqexts v3_req_0
openssl x509 -req -sha256 -CA rootca.crt -CAkey rootca.key -passin $PASS -CAserial $SERIAL -in intermediate0.csr -out intermediate0.crt -extfile $CONF -extensions v3_ca_0
openssl x509 -req -sha256 -CA intermediate0.crt -CAkey intermediate0.key -passin $PASS -CAserial $SERIAL -in server.csr -out server.crt -extfile $CONF -extensions server

# Create certificate chain where intermediate certificate cannot sign other intermediate certificates
openssl req -new -keyout intermediate.key -out intermediate.csr -subj '/CN=Intermediate' -passout $PASS -config $CONF -reqexts v3_req
openssl x509 -req -sha256 -CA intermediate0.crt -CAkey intermediate0.key -passin $PASS -CAserial $SERIAL -in intermediate.csr -out intermediate.crt -extfile $CONF -extensions v3_ca
openssl x509 -req -sha256 -CA intermediate.crt -CAkey intermediate.key -passin $PASS -CAserial $SERIAL -in server.csr -out server0.crt -extfile $CONF -extensions server
