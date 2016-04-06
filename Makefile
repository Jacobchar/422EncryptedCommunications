DEPS = Crypto.h

Crypto: lib_crypto.c
	make compile
	make header
	gcc -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" -shared -std=c99 -fpic -o libcrypto.so lib_crypto.c
	make export

compile:
	javac *.java

header:
	javah Crypto

export:
	export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.

clean:
	rm *.so *.class *.h

