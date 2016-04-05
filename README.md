# 422EncryptedCommunications
This project is designed to explore the usage of a realistic Feistel block cipher by creating a secure communications channel between a server and one or more clients. The file server program will receive requests for filenames over a socket and pass that file to the requesting client. These requests and responses will be kept secure by encrypting them useing the Tiney Encryption Algorithm (TEA) cipher. Each potential client will possess a single key for communicating with the server and the server will keep a list of valid keys. We will assume that these keys have already been securely distributed.


Help for Client/Server socket communications: http://stackoverflow.com/questions/1776457/java-client-server-application-with-sockets
