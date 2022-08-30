package crypto

/*
#cgo linux LDFLAGS: -L../output/lib -lcrypto -Wl,-rpath=../output/lib
#cgo darwin LDFLAGS: -L../output/lib -lcrypto
#include "../output/include/crypto.h"
*/
import "C"
import (
	"bytes"
	"errors"
	"math/big"
	"strconv"
	"unsafe"
)

func Sign(privateKey, msgHash, seed *big.Int) (r, s *big.Int, err error) {
	if msgHash == nil || privateKey == nil {
		return nil, nil, errors.New("msgHash or privateKey is nil")
	}

	doc := C.SignDocument{
		msg_hash:    C.CString(msgHash.Text(16)),
		private_key: C.CString(privateKey.Text(16)),
	}

	if seed == nil {
		doc.seed = nil
	} else {
		doc.seed = C.CString(seed.Text(16))
	}

	ret := C.SignResult{
		r: (*C.char)(C.malloc(C.size_t(C.BIG_INT_SIZE))),
		s: (*C.char)(C.malloc(C.size_t(C.BIG_INT_SIZE))),
	}
	defer func() {
		C.free(unsafe.Pointer(ret.r))
		C.free(unsafe.Pointer(ret.s))
	}()

	errno := C.sign(doc, ret)

	if errno != C.Ok {
		return nil, nil, errors.New(C.GoString(C.explain(errno)))
	}

	r, s = new(big.Int), new(big.Int)
	_, ok := r.SetString(C.GoString(ret.r), 16)
	if !ok {
		return nil, nil, errors.New("r is not a valid big.Int")
	}
	_, ok = s.SetString(C.GoString(ret.s), 16)
	if !ok {
		return nil, nil, errors.New("s is not a valid big.Int")
	}
	return r, s, nil
}

func Verify(publicKey, msgHash, r, s *big.Int) (bool, error) {
	if msgHash == nil || publicKey == nil || r == nil || s == nil {
		return false, errors.New("arguments cannot be nil")
	}

	signuture := C.Signature{
		msg_hash:   C.CString(msgHash.Text(16)),
		public_key: C.CString(publicKey.Text(16)),
		r:          C.CString(r.Text(16)),
		s:          C.CString(s.Text(16)),
	}

	valid := C.bool(false)
	errno := C.verify(signuture, &valid)
	if errno != C.Ok {
		return false, errors.New(C.GoString(C.explain(errno)))
	}

	return bool(valid), nil
}

func GetPublicKey(privateKey *big.Int) (publicKey *big.Int, err error) {
	if privateKey == nil {
		return nil, errors.New("privateKey is nil")
	}

	privateStr := privateKey.Text(16)
	publicStr := (*C.char)(C.malloc(C.size_t(C.BIG_INT_SIZE)))
	defer C.free(unsafe.Pointer(publicStr))

	errno := C.get_public_key(C.CString(privateStr), publicStr)

	if errno != C.Ok {
		return nil, errors.New(C.GoString(C.explain(errno)))
	}

	publicKey = new(big.Int)
	_, ok := publicKey.SetString(C.GoString(publicStr), 16)
	if !ok {
		return nil, errors.New("publicKey is not a valid big.Int")
	}
	return publicKey, nil
}

func GetPrivateKeyFromEthSignature(ethSignature *big.Int) (privateKey *big.Int, err error) {
	if ethSignature == nil {
		return nil, errors.New("ethSignature is nil")
	}

	privateKeyCStr := make([]byte, 65)
	errno := C.get_private_key_from_eth_signature(C.CString(ethSignature.Text(16)), (*C.char)(unsafe.Pointer(&privateKeyCStr[0])))
	if errno != C.Ok {
		return nil, errors.New(C.GoString(C.explain(errno)))
	}

	// though it should always be 64
	privateKeyLength := bytes.IndexByte(privateKeyCStr[:], 0)
	privateKeyStr := string(privateKeyCStr[:privateKeyLength])

	privateKey = new(big.Int)
	_, ok := privateKey.SetString(privateKeyStr, 16)
	if !ok {
		return nil, errors.New("private_key is not a valid big.Int")
	}
	return privateKey, nil
}

func GetTransferMsgHash(
	amount int,
	nonce int,
	senderVaultID *big.Int,
	token *big.Int,
	receiverVaultID int,
	receiverPublicKey *big.Int,
	expirationTimeStamp int,
	condition *big.Int,
) (result *big.Int, err error) {
	msg := C.TransferMsg{
		amount:                C.CString(strconv.Itoa(amount)),
		nonce:                 C.CString(strconv.Itoa(nonce)),
		sender_vault_id:       C.CString(senderVaultID.Text(16)),
		token:                 C.CString(token.Text(16)),
		receiver_vault_id:     C.CString(strconv.Itoa(receiverVaultID)),
		receiver_public_key:   C.CString(receiverPublicKey.Text(16)),
		expiration_time_stamp: C.CString(strconv.Itoa(expirationTimeStamp)),
		condition:             C.CString(condition.Text(16)),
	}
	hash := (*C.char)(C.malloc(C.size_t(C.BIG_INT_SIZE)))
	defer func() {
		C.free(unsafe.Pointer(hash))
	}()
	errno := C.get_transfer_msg_hash(msg, hash)
	if errno != C.Ok {
		return nil, errors.New(C.GoString(C.explain(errno)))
	}
	result = new(big.Int)
	_, ok := result.SetString(C.GoString(hash), 16)
	if !ok {
		return nil, errors.New("hash is not a valid big.Int")
	}
	return result, err
}

func GetLimitOrderMsgHash(
	vaultSell int,
	vaultBut int,
	amountSell int,
	amountBuy int,
	tokenSell *big.Int,
	tokenBuy *big.Int,
	nonce int,
	expirationTimeStamp int,
) (result *big.Int, err error) {
	msg := C.LimitOrderMsg{
		vault_sell:            C.CString(strconv.Itoa(vaultSell)),
		vault_buy:             C.CString(strconv.Itoa(vaultBut)),
		amount_sell:           C.CString(strconv.Itoa(amountSell)),
		amount_buy:            C.CString(strconv.Itoa(amountBuy)),
		token_sell:            C.CString(tokenSell.Text(16)),
		token_buy:             C.CString(tokenBuy.Text(16)),
		nonce:                 C.CString(strconv.Itoa(nonce)),
		expiration_time_stamp: C.CString(strconv.Itoa(expirationTimeStamp)),
	}
	hash := (*C.char)(C.malloc(C.size_t(C.BIG_INT_SIZE)))
	defer func() {
		C.free(unsafe.Pointer(hash))
	}()
	errno := C.get_limit_order_msg_hash(msg, hash)
	if errno != C.Ok {
		return nil, errors.New(C.GoString(C.explain(errno)))
	}
	result = new(big.Int)
	_, ok := result.SetString(C.GoString(hash), 16)
	if !ok {
		return nil, errors.New("hash is not a valid big.Int")
	}
	return result, err
}
