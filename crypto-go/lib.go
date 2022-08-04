package crypto

/*
#cgo LDFLAGS: -L../output/lib -lcrypto
#include "../output/include/crypto.h"
*/
import "C"
import (
	"errors"
	"math/big"
	"unsafe"
)

func Sign(msgHash, privateKey, Seed *big.Int) (r, s *big.Int, err error) {
	if msgHash == nil || privateKey == nil || Seed == nil {
		return nil, nil, errors.New("msgHash, privateKey or Seed is nil")
	}

	sign := C.sign(C.ECDocument{
		msg_hash:    C.CString(msgHash.Text(16)),
		private_key: C.CString(privateKey.Text(16)),
		seed:        C.CString(Seed.Text(16)),
	})

	defer func() {
		C.free(unsafe.Pointer(sign.r))
		C.free(unsafe.Pointer(sign.s))
		C.free(unsafe.Pointer(sign.err))
	}()

	if sign.err != nil {
		return nil, nil, errors.New(C.GoString(sign.err))
	}
	r, s = new(big.Int), new(big.Int)
	_, ok := r.SetString(C.GoString(sign.r), 16)
	if !ok {
		return nil, nil, errors.New("r is not a valid big.Int")
	}
	_, ok = s.SetString(C.GoString(sign.s), 16)
	if !ok {
		return nil, nil, errors.New("s is not a valid big.Int")
	}
	return r, s, nil
}
