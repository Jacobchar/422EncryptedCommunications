#include <jni.h>
#include "Crypto.h"

void encrypt (long *v, long *k) {
/* TEA encryption algorithm */
unsigned long y = v[0], z=v[1], sum = 0;
unsigned long delta = 0x9e3779b9, n=32;

	while (n-- > 0) {
		sum += delta;
		y += (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		z += (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
	}

	v[0] = y;
	v[1] = z;
}

void decrypt (long *v, long *k) {
/* TEA decryption routine */
unsigned long n=32, sum, y=v[0], z=v[1];
unsigned long delta=0x9e3779b9l;

	sum = delta<<5;
	while (n-- > 0) {
		z -= (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
		y -= (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		sum -= delta;
	}
	v[0] = y;
	v[1] = z;
}

JNIEXPORT void JNICALL Java_Crypto_encrypt (JNIEnv * env, jclass jcl, jbyteArray jmsg, jlongArray jid) {

	jsize length = (*env)->GetArrayLength(env, jmsg);
	jbyte *msg = (*env)->GetByteArrayElements(env, jmsg, NULL);
	jlong *id = (*env)->GetLongArrayElements(env, jid, NULL);

	for (int i = 0; i < (length - 8); i += 16) {
		encrypt((long *) &msg[i], (long *) id);
	}

	(*env)->ReleaseByteArrayElements(env, jmsg, msg, 0);
}


JNIEXPORT void JNICALL Java_Crypto_decrypt (JNIEnv * env, jclass jcl, jbyteArray jmsg, jlongArray jid) {

	
	jsize length = (*env)->GetArrayLength(env, jmsg);
	jbyte *msg = (*env)->GetByteArrayElements(env, jmsg, NULL);
	jlong *id = (*env)->GetLongArrayElements(env, jid, NULL);

	for (int i = 0; i < (length - 8); i += 16) {
		decrypt((long *) &msg[i], (long *) id);
	}

	(*env)->ReleaseByteArrayElements(env, jmsg, msg, 0);
}
