sudo apt-get install libnss3-tools
certutil -d sql:$HOME/.pki/nssdb -A -t TC -n "Sahi" -i ../certgen/X509CA/ca/new_ca.crt
