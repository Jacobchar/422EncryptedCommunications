/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class Crypto */

#ifndef _Included_Crypto
#define _Included_Crypto
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     Crypto
 * Method:    encrypt
 * Signature: ([B[J)V
 */
JNIEXPORT void JNICALL Java_Crypto_encrypt
  (JNIEnv *, jclass, jbyteArray, jlongArray);

/*
 * Class:     Crypto
 * Method:    decrypt
 * Signature: ([B[J)V
 */
JNIEXPORT void JNICALL Java_Crypto_decrypt
  (JNIEnv *, jclass, jbyteArray, jlongArray);

#ifdef __cplusplus
}
#endif
#endif