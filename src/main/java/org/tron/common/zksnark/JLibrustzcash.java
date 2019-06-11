package org.tron.common.zksnark;

import com.sun.jna.Library;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.tron.common.utils.ByteArray;
import org.tron.common.zksnark.LibrustzcashParam.BindingSigParams;
import org.tron.common.zksnark.LibrustzcashParam.CheckOutputParams;
import org.tron.common.zksnark.LibrustzcashParam.CheckSpendParams;
import org.tron.common.zksnark.LibrustzcashParam.ComputeCmParams;
import org.tron.common.zksnark.LibrustzcashParam.ComputeNfParams;
import org.tron.common.zksnark.LibrustzcashParam.CrhIvkParams;
import org.tron.common.zksnark.LibrustzcashParam.FinalCheckParams;
import org.tron.common.zksnark.LibrustzcashParam.InitZksnarkParams;
import org.tron.common.zksnark.LibrustzcashParam.IvkToPkdParams;
import org.tron.common.zksnark.LibrustzcashParam.KaAgreeParams;
import org.tron.common.zksnark.LibrustzcashParam.KaDerivepublicParams;
import org.tron.common.zksnark.LibrustzcashParam.MerkleHashParams;
import org.tron.common.zksnark.LibrustzcashParam.OutputProofParams;
import org.tron.common.zksnark.LibrustzcashParam.SpendProofParams;
import org.tron.common.zksnark.LibrustzcashParam.SpendSigParams;
import org.tron.common.zksnark.LibrustzcashParam.Zip32XfvkAddressParams;
import org.tron.common.zksnark.LibrustzcashParam.Zip32XskDeriveParams;
import org.tron.common.zksnark.LibrustzcashParam.Zip32XskMasterParams;
import org.tron.core.config.args.Args;
import org.tron.core.exception.ZksnarkException;

@Slf4j
public class JLibrustzcash {

  private static final Librustzcash INSTANCE = LibrustzcashWrapper.getInstance();

  private static final Map<String, String> libraries = new ConcurrentHashMap<>();

  public static void librustzcashZip32XskMaster(Zip32XskMasterParams params) {
    if (!isOpenZen()) {
      return;
    }
    INSTANCE.librustzcashZip32XskMaster(params.getData(), params.getSize(), params.getM_bytes());
  }

  public static void librustzcashInitZksnarkParams(InitZksnarkParams params) {
    if (!isOpenZen()) {
      return;
    }
    INSTANCE.librustzcashInitZksnarkParams(params.getSpend_path(),
        params.getSpend_hash(), params.getOutput_path(), params.getOutput_hash());
  }

  public static void librustzcashZip32XskDerive(Zip32XskDeriveParams params) {
    if (!isOpenZen()) {
      return;
    }
    INSTANCE.librustzcashZip32XskDerive(params.getData(), params.getSize(), params.getM_bytes());
  }

  public static boolean librustzcashZip32XfvkAddress(Zip32XfvkAddressParams params) {
    if (!isOpenZen()) {
      return true;
    }
    return INSTANCE.librustzcashZip32XfvkAddress(params.getXfvk(), params.getJ(),
        params.getJ_ret(), params.getAddr_ret());
  }

  public static void librustzcashCrhIvk(CrhIvkParams params) {
    if (!isOpenZen()) {
      return;
    }
    INSTANCE.librustzcashCrhIvk(params.getAk(), params.getNk(), params.getIvk());
  }

  public static boolean librustzcashKaAgree(KaAgreeParams params) {
    if (!isOpenZen()) {
      return true;
    }
    return INSTANCE.librustzcashSaplingKaAgree(params.getP(), params.getSk(), params.getResult());
  }

  public static boolean librustzcashComputeCm(ComputeCmParams params) {
    if (!isOpenZen()) {
      return true;
    }
    return INSTANCE.librustzcashSaplingComputeCm(params.getD(), params.getPk_d(),
        params.getValue(), params.getR(), params.getCm());
  }

  public static boolean librustzcashComputeNf(ComputeNfParams params) {
    if (!isOpenZen()) {
      return true;
    }
    INSTANCE.librustzcashSaplingComputeNf(params.getD(), params.getPk_d(), params.getValue(),
        params.getR(), params.getAk(), params.getNk(), params.getPosition(), params.getResult());
    return true;
  }

  /**
   * @param ask: the spend authorizing key,to genarate ak, 32 bytes
   * @return ak 32 bytes
   */
  public static byte[] librustzcashAskToAk(byte[] ask) throws ZksnarkException {
    if (!isOpenZen()) {
      return ByteArray.EMPTY_BYTE_ARRAY;
    }
    LibrustzcashParam.valid32Params(ask);
    byte[] ak = new byte[32];
    INSTANCE.librustzcashAskToAk(ask, ak);
    return ak;
  }

  /**
   * @param nsk: the proof authorizing key, to genarate nk, 32 bytes
   * @return 32 bytes
   */
  public static byte[] librustzcashNskToNk(byte[] nsk) throws ZksnarkException {
    if (!isOpenZen()) {
      return ByteArray.EMPTY_BYTE_ARRAY;
    }
    LibrustzcashParam.valid32Params(nsk);
    byte[] nk = new byte[32];
    INSTANCE.librustzcashNskToNk(nsk, nk);
    return nk;
  }

  // void librustzcash_nsk_to_nk(const unsigned char *nsk, unsigned char *result);

  /**
   * @return r: random number, less than r_J,   32 bytes
   */
  public static byte[] librustzcashSaplingGenerateR(byte[] r) throws ZksnarkException {
    if (!isOpenZen()) {
      return ByteArray.EMPTY_BYTE_ARRAY;
    }
    LibrustzcashParam.valid32Params(r);
    INSTANCE.librustzcashSaplingGenerateR(r);
    return r;
  }

  public static boolean librustzcashSaplingKaDerivepublic(KaDerivepublicParams params) {
    if (!isOpenZen()) {
      return true;
    }
    return INSTANCE.librustzcashSaplingKaDerivepublic(params.getDiversifier(), params.getEsk(),
        params.getResult());
  }

  public static long librustzcashSaplingProvingCtxInit() {
    if (!isOpenZen()) {
      return 0;
    }
    return INSTANCE.librustzcashSaplingProvingCtxInit();
  }

  /**
   * check validity of d
   *
   * @param d: 11 bytes
   */
  public static boolean librustzcashCheckDiversifier(byte[] d) throws ZksnarkException {
    if (!isOpenZen()) {
      return true;
    }
    LibrustzcashParam.valid11Params(d);
    return INSTANCE.librustzcashCheckDiversifier(d);
  }

  public static boolean librustzcashSaplingSpendProof(SpendProofParams params) {
    if (!isOpenZen()) {
      return true;
    }
    return INSTANCE.librustzcashSaplingSpendProof(params.getCtx(), params.getAk(),
        params.getNsk(), params.getD(), params.getR(), params.getAlpha(), params.getValue(),
        params.getAnchor(), params.getVoucherPath(), params.getCv(), params.getRk(),
        params.getZkproof());
  }

  public static boolean librustzcashSaplingOutputProof(OutputProofParams params) {
    if (!isOpenZen()) {
      return true;
    }
    return INSTANCE.librustzcashSaplingOutputProof(params.getCtx(), params.getEsk(),
        params.getD(), params.getPk_d(), params.getR(), params.getValue(), params.getCv(),
        params.getZkproof());
  }

  public static boolean librustzcashSaplingSpendSig(SpendSigParams params) {
    if (!isOpenZen()) {
      return true;
    }
    return INSTANCE.librustzcashSaplingSpendSig(params.getAsk(), params.getAlpha(),
        params.getSigHash(), params.getResult());
  }

  public static boolean librustzcashSaplingBindingSig(BindingSigParams params) {
    if (!isOpenZen()) {
      return true;
    }
    return INSTANCE.librustzcashSaplingBindingSig(params.getCtx(),
        params.getValueBalance(), params.getSighash(), params.getResult());
  }

  /**
   * convert value to 32-byte scalar
   *
   * @param value: 64 bytes
   * @param data: return, 32 bytes
   */
  public static void librustzcashToScalar(byte[] value, byte[] data) throws ZksnarkException {
    if (!isOpenZen()) {
      return;
    }
    LibrustzcashParam.validParamLength(value, 64);
    LibrustzcashParam.valid32Params(data);
    INSTANCE.librustzcashToScalar(value, data);
  }

  public static void librustzcashSaplingProvingCtxFree(long ctx) {
    if (!isOpenZen()) {
      return;
    }
    INSTANCE.librustzcashSaplingProvingCtxFree(ctx);
  }

  public static long librustzcashSaplingVerificationCtxInit() {
    if (!isOpenZen()) {
      return 0;
    }
    return INSTANCE.librustzcashSaplingVerificationCtxInit();
  }

  public static boolean librustzcashSaplingCheckSpend(CheckSpendParams params) {
    if (!isOpenZen()) {
      return true;
    }
    return INSTANCE.librustzcashSaplingCheckSpend(params.getCtx(), params.getCv(),
        params.getAnchor(), params.getNullifier(), params.getRk(), params.getZkproof(),
        params.getSpendAuthSig(), params.getSighashValue());
  }

  public static boolean librustzcashSaplingCheckOutput(CheckOutputParams params) {
    if (!isOpenZen()) {
      return true;
    }
    return INSTANCE.librustzcashSaplingCheckOutput(params.getCtx(), params.getCv(),
        params.getCm(), params.getEphemeralKey(), params.getZkproof());
  }

  public static boolean librustzcashSaplingFinalCheck(FinalCheckParams params) {
    if (!isOpenZen()) {
      return true;
    }
    return INSTANCE.librustzcashSaplingFinalCheck(params.getCtx(),
        params.getValueBalance(), params.getBindingSig(), params.getSighashValue());
  }

  public static void librustzcashSaplingVerificationCtxFree(long ctx) {
    if (!isOpenZen()) {
      return;
    }
    INSTANCE.librustzcashSaplingVerificationCtxFree(ctx);
  }

  public static boolean librustzcashIvkToPkd(IvkToPkdParams params) {
    if (!isOpenZen()) {
      return true;
    }
    return INSTANCE.librustzcashIvkToPkd(params.getIvk(), params.getD(), params.getPk_d());
  }

  public static void librustzcashMerkleHash(MerkleHashParams params) {
    if (!isOpenZen()) {
      return;
    }
    INSTANCE.librustzcashMerkleHash(params.getDepth(), params.getA(), params.getB(),
        params.getResult());
  }

  /**
   * @param result: uncommitted value, 32 bytes
   */
  public static void librustzcash_tree_uncommitted(byte[] result) throws ZksnarkException {
    if (!isOpenZen()) {
      return;
    }
    LibrustzcashParam.valid32Params(result);
    INSTANCE.librustzcashTreeUncommitted(result);
  }

  private static boolean isOpenZen() {
    return Args.getInstance().isAllowShieldedTransaction();
  }

  public static String getLibraryByName(String name) {
    return libraries.computeIfAbsent(name, JLibrustzcash::getLibrary);
  }

  private static String getLibrary(String name) {
    String platform;
    String extension;
    if (Platform.isLinux()) {
      platform = "linux";
      extension = ".so";
    } else if (Platform.isWindows()) {
      platform = "windows";
      extension = ".dll";
    } else if (Platform.isMac()) {
      platform = "macos";
      extension = ".dylib";
    } else {
      throw new RuntimeException("unsupportedPlatformException");
    }
    InputStream in = JLibrustzcash.class.getClassLoader().getResourceAsStream(
        "native-package" + File.separator + platform + File.separator + name + extension);
    File fileOut = new File(
        System.getProperty("java.io.tmpdir") + File.separator + name + "-" + System
            .currentTimeMillis() + extension);
    try {
      FileUtils.copyToFile(in, fileOut);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
    return fileOut.getAbsolutePath();
  }

  public interface ILibrustzcash extends Library {

    void librustzcash_init_zksnark_params(byte[] spend_path, int spend_path_len, String spend_hash,
        byte[] output_path, int output_path_len, String output_hash);

    void librustzcash_zip32_xsk_master(byte[] data, int size, byte[] m_bytes);

    void librustzcash_zip32_xsk_derive(byte[] xsk_parent, int i, byte[] xsk_i);

    boolean librustzcash_zip32_xfvk_address(byte[] xfvk, byte[] j, byte[] j_ret, byte[] addr_ret);

    void librustzcash_ask_to_ak(byte[] ask, byte[] result);

    void librustzcash_sapling_compute_nf(byte[] d, byte[] pk_d, long value_, byte[] r, byte[] ak,
        byte[] nk, long position, byte[] result);

    void librustzcash_nsk_to_nk(byte[] nsk, byte[] result);

    void librustzcash_sapling_generate_r(byte[] r);

    boolean librustzcash_sapling_ka_derivepublic(byte[] diversifier, byte[] esk, byte[] result);

    void librustzcash_crh_ivk(byte[] ak, byte[] nk, byte[] result);

    boolean librustzcash_sapling_ka_agree(byte[] p, byte[] sk, byte[] result);

    boolean librustzcash_check_diversifier(byte[] diversifier);

    boolean librustzcash_ivk_to_pkd(byte[] ivk, byte[] diversifier, byte[] result);

    boolean librustzcash_sapling_compute_cm(byte[] diversifier, byte[] pk_d, long value, byte[] r,
        byte[] result);
    //bool librustzcash_ivk_to_pkd(const unsigned char *ivk, const unsigned char *diversifier, unsigned char *result);
//    bool librustzcash_sapling_compute_cm(
//        const unsigned char *diversifier,
//        const unsigned char *pk_d,
//        const uint64_t value,
//        const unsigned char *r,
//        unsigned char *result
//    );

    Pointer librustzcash_sapling_proving_ctx_init();

    boolean librustzcash_sapling_spend_proof(Pointer ctx, byte[] ak, byte[] nsk, byte[] diversifier,
        byte[] rcm, byte[] ar, long value, byte[] anchor, byte[] witness, byte[] cv, byte[] rk,
        byte[] zkproof);

    boolean librustzcash_sapling_output_proof(Pointer ctx, byte[] esk, byte[] diversifier,
        byte[] pk_d, byte[] rcm, long value, byte[] cv, byte[] zkproof);

    boolean librustzcash_sapling_spend_sig(byte[] ask, byte[] ar, byte[] sighash, byte[] result);

    boolean librustzcash_sapling_binding_sig(Pointer ctx, long valueBalance, byte[] sighash,
        byte[] result);

    void librustzcash_sapling_proving_ctx_free(Pointer ctx);

    Pointer librustzcash_sapling_verification_ctx_init();

    boolean librustzcash_sapling_check_spend(Pointer ctx, byte[] cv, byte[] anchor,
        byte[] nullifier, byte[] rk, byte[] zkproof, byte[] spendAuthSig, byte[] sighashValue);

    boolean librustzcash_sapling_check_output(Pointer ctx, byte[] cv, byte[] cm,
        byte[] ephemeralKey, byte[] zkproof);

    boolean librustzcash_sapling_final_check(Pointer ctx, long valueBalance, byte[] bindingSig,
        byte[] sighashValue);

    void librustzcash_sapling_verification_ctx_free(Pointer ctx);

    /// Computes a merkle tree hash for a given depth.
    /// The `depth` parameter should not be larger than
    /// 62.
    ///
    /// `a` and `b` each must be of length 32, and must each
    /// be scalars of BLS12-381.
    ///
    /// The result of the merkle tree hash is placed in
    /// `result`, which must also be of length 32.
    void librustzcash_merkle_hash(int depth, byte[] a, byte[] b, byte[] result
    );

    /// Writes the "uncommitted" note value for empty leaves
    /// of the merkle tree. `result` must be a valid pointer
    /// to 32 bytes which will be written.
    void librustzcash_tree_uncommitted(byte[] result);

    void librustzcash_to_scalar(byte[] input, byte[] result);
  }
}