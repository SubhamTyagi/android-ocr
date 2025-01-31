| Index | Tesseract parameters | Default Value| Details |
| ------------- |:-------------:| -----:|-----:|
| 1 | editor_image_xpos | 590 | Editor image X Pos |
| 2 | editor_image_ypos | 10 | Editor image Y Pos |
| 3 | editor_image_menuheight | 50 | Add to image height for menu bar |
| 4 | editor_image_word_bb_color | 7 | Word bounding box colour |
| 5 | editor_image_blob_bb_color | 4 | Blob bounding box colour |
| 6 | editor_word_xpos | 60 | Word window X Pos |
| 7 | editor_word_ypos | 510 | Word window Y Pos |
| 8 | editor_word_height | 240 | Word window height |
| 9 | editor_word_width | 655 | Word window width |
| 10 | log_level | 2147483647 | Logging level |
| 11 | classify_num_cp_levels | 3 | Number of Class Pruner Levels |
| 12 | textord_debug_tabfind | 0 | Debug tab finding |
| 13 | textord_debug_bugs | 0 | Turn on output related to bugs in tab finding |
| 14 | textord_testregion_left | -1 | Left edge of debug reporting rectangle in Leptonica coords (bottom=0/top=height), with horizontal lines x/y-flipped |
| 15 | textord_testregion_top | 2147483647 | Top edge of debug reporting rectangle in Leptonica coords (bottom=0/top=height), with horizontal lines x/y-flipped |
| 16 | textord_testregion_right | 2147483647 | Right edge of debug rectangle in Leptonica coords (bottom=0/top=height), with horizontal lines x/y-flipped |
| 17 | textord_testregion_bottom | -1 | Bottom edge of debug rectangle in Leptonica coords (bottom=0/top=height), with horizontal lines x/y-flipped |
| 18 | textord_tabfind_show_partitions | 0 | Show partition bounds, waiting if >1 (ScrollView) |
| 19 | devanagari_split_debuglevel | 0 | Debug level for split shiro-rekha process. |
| 20 | edges_max_children_per_outline | 10 | Max number of children inside a character outline |
| 21 | edges_max_children_layers | 5 | Max layers of nested children inside a character outline |
| 22 | edges_children_per_grandchild | 10 | Importance ratio for chucking outlines |
| 23 | edges_children_count_limit | 45 | Max holes allowed in blob |
| 24 | edges_min_nonhole | 12 | Min pixels for potential char in box |
| 25 | edges_patharea_ratio | 40 | Max lensq/area for acceptable child outline |
| 26 | textord_fp_chop_error | 2 | Max allowed bending of chop cells |
| 27 | textord_tabfind_show_images | 0 | Show image blobs |
| 28 | textord_skewsmooth_offset | 4 | For smooth factor |
| 29 | textord_skewsmooth_offset2 | 1 | For smooth factor |
| 30 | textord_test_x | -2147483647 | coord of test pt |
| 31 | textord_test_y | -2147483647 | coord of test pt |
| 32 | textord_min_blobs_in_row | 4 | Min blobs before gradient counted |
| 33 | textord_spline_minblobs | 8 | Min blobs in each spline segment |
| 34 | textord_spline_medianwin | 6 | Size of window for spline segmentation |
| 35 | textord_max_blob_overlaps | 4 | Max number of blobs a big blob can overlap |
| 36 | textord_min_xheight | 10 | Min credible pixel xheight |
| 37 | textord_lms_line_trials | 12 | Number of linew fits to do |
| 38 | oldbl_holed_losscount | 10 | Max lost before fallback line used |
| 39 | pitsync_linear_version | 6 | Use new fast algorithm |
| 40 | textord_tabfind_show_strokewidths | 0 | Show stroke widths (ScrollView) |
| 41 | textord_dotmatrix_gap | 3 | Max pixel gap for broken pixed pitch |
| 42 | textord_debug_block | 0 | Block to do debug on |
| 43 | textord_pitch_range | 2 | Max range test on pitch |
| 44 | textord_words_veto_power | 5 | Rows required to outvote a veto |
| 45 | curl_timeout | 0 | Timeout for curl in seconds |
| 46 | equationdetect_save_bi_image | 0 | Save input bi image |
| 47 | equationdetect_save_spt_image | 0 | Save special character image |
| 48 | equationdetect_save_seed_image | 0 | Save the seed image |
| 49 | equationdetect_save_merged_image | 0 | Save the merged image |
| 50 | poly_debug | 0 | Debug old poly |
| 51 | poly_wide_objects_better | 1 | More accurate approx on wide things |
| 52 | wordrec_display_splits | 0 | Display splits |
| 53 | textord_debug_printable | 0 | Make debug windows printable |
| 54 | textord_space_size_is_variable | 0 | If true, word delimiter spaces are assumed to have variable width, even though characters have fixed pitch. |
| 55 | textord_tabfind_show_initial_partitions | 0 | Show partition bounds |
| 56 | textord_tabfind_show_reject_blobs | 0 | Show blobs rejected as noise |
| 57 | textord_tabfind_show_columns | 0 | Show column bounds (ScrollView) |
| 58 | textord_tabfind_show_blocks | 0 | Show final block bounds (ScrollView) |
| 59 | textord_tabfind_find_tables | 1 | run table detection |
| 60 | devanagari_split_debugimage | 0 | Whether to create a debug image for split shiro-rekha process. |
| 61 | textord_show_fixed_cuts | 0 | Draw fixed pitch cell boundaries |
| 62 | edges_use_new_outline_complexity | 0 | Use the new outline complexity module |
| 63 | edges_debug | 0 | turn on debugging for this module |
| 64 | edges_children_fix | 0 | Remove boxy parents of char-like children |
| 65 | gapmap_debug | 0 | Say which blocks have tables |
| 66 | gapmap_use_ends | 0 | Use large space at start and end of rows |
| 67 | gapmap_no_isolated_quanta | 0 | Ensure gaps not less than 2quanta wide |
| 68 | textord_heavy_nr | 0 | Vigorously remove noise |
| 69 | textord_show_initial_rows | 0 | Display row accumulation |
| 70 | textord_show_parallel_rows | 0 | Display page correlated rows |
| 71 | textord_show_expanded_rows | 0 | Display rows after expanding |
| 72 | textord_show_final_rows | 0 | Display rows after final fitting |
| 73 | textord_show_final_blobs | 0 | Display blob bounds after pre-ass |
| 74 | textord_test_landscape | 0 | Tests refer to land/port |
| 75 | textord_parallel_baselines | 1 | Force parallel baselines |
| 76 | textord_straight_baselines | 0 | Force straight baselines |
| 77 | textord_old_baselines | 1 | Use old baseline algorithm |
| 78 | textord_old_xheight | 0 | Use old xheight algorithm |
| 79 | textord_fix_xheight_bug | 1 | Use spline baseline |
| 80 | textord_fix_makerow_bug | 1 | Prevent multiple baselines |
| 81 | textord_debug_xheights | 0 | Test xheight algorithms |
| 82 | textord_biased_skewcalc | 1 | Bias skew estimates with line length |
| 83 | textord_interpolating_skew | 1 | Interpolate across gaps |
| 84 | textord_new_initial_xheight | 1 | Use test xheight mechanism |
| 85 | textord_debug_blob | 0 | Print test blob information |
| 86 | textord_really_old_xheight | 0 | Use original wiseowl xheight |
| 87 | textord_oldbl_debug | 0 | Debug old baseline generation |
| 88 | textord_debug_baselines | 0 | Debug baseline generation |
| 89 | textord_oldbl_paradef | 1 | Use para default mechanism |
| 90 | textord_oldbl_split_splines | 1 | Split stepped splines |
| 91 | textord_oldbl_merge_parts | 1 | Merge suspect partitions |
| 92 | oldbl_corrfix | 1 | Improve correlation of heights |
| 93 | oldbl_xhfix | 0 | Fix bug in modes threshold for xheights |
| 94 | textord_ocropus_mode | 0 | Make baselines for ocropus |
| 95 | textord_tabfind_only_strokewidths | 0 | Only run stroke widths |
| 96 | textord_tabfind_show_initialtabs | 0 | Show tab candidates |
| 97 | textord_tabfind_show_finaltabs | 0 | Show tab vectors |
| 98 | textord_show_tables | 0 | Show table regions (ScrollView) |
| 99 | textord_tablefind_show_mark | 0 | Debug table marking steps in detail (ScrollView) |
| 100 | textord_tablefind_show_stats | 0 | Show page stats used in table finding (ScrollView) |
| 101 | textord_tablefind_recognize_tables | 0 | Enables the table recognizer for table layout and filtering. |
| 102 | textord_all_prop | 0 | All doc is proportial text |
| 103 | textord_debug_pitch_test | 0 | Debug on fixed pitch test |
| 104 | textord_disable_pitch_test | 0 | Turn off dp fixed pitch algorithm |
| 105 | textord_fast_pitch_test | 0 | Do even faster pitch algorithm |
| 106 | textord_debug_pitch_metric | 0 | Write full metric stuff |
| 107 | textord_show_row_cuts | 0 | Draw row-level cuts |
| 108 | textord_show_page_cuts | 0 | Draw page-level cuts |
| 109 | textord_blockndoc_fixed | 0 | Attempt whole doc/block fixed pitch |
| 110 | textord_show_initial_words | 0 | Display separate words |
| 111 | textord_blocksall_fixed | 0 | Moan about prop blocks |
| 112 | textord_blocksall_prop | 0 | Moan about fixed pitch blocks |
| 113 | textord_pitch_scalebigwords | 0 | Scale scores on big words |
| 114 | textord_restore_underlines | 1 | Chop underlines & put back |
| 115 | textord_force_make_prop_words | 0 | Force proportional word segmentation on all rows |
| 116 | textord_chopper_test | 0 | Chopper is being tested. |
| 117 | wordrec_display_all_blobs | 0 | Display Blobs |
| 118 | wordrec_blob_pause | 0 | Blob pause |
| 119 | stream_filelist | 0 | Stream a filelist from stdin |
| 120 | editor_image_win_name | EditorImage | Editor image window name |
| 121 | editor_word_name | BlnWords | BL normalized word window |
| 122 | debug_file |  | File to send tprintf output to |
| 123 | document_title |  | Title of output document (used for hOCR and PDF output) |
| 124 | curl_cookiefile |  | File with cookie data for curl |
| 125 | classify_font_name | UnknownFont | Default font name to be used in training |
| 126 | dotproduct | generic | Function used for calculation of dot product |
| 127 | classify_cp_angle_pad_loose | 45 | Class Pruner Angle Pad Loose |
| 128 | classify_cp_angle_pad_medium | 20 | Class Pruner Angle Pad Medium |
| 129 | classify_cp_angle_pad_tight | 10 | CLass Pruner Angle Pad Tight |
| 130 | classify_cp_end_pad_loose | 0.5 | Class Pruner End Pad Loose |
| 131 | classify_cp_end_pad_medium | 0.5 | Class Pruner End Pad Medium |
| 132 | classify_cp_end_pad_tight | 0.5 | Class Pruner End Pad Tight |
| 133 | classify_cp_side_pad_loose | 2.5 | Class Pruner Side Pad Loose |
| 134 | classify_cp_side_pad_medium | 1.2 | Class Pruner Side Pad Medium |
| 135 | classify_cp_side_pad_tight | 0.6 | Class Pruner Side Pad Tight |
| 136 | classify_pp_angle_pad | 45 | Proto Pruner Angle Pad |
| 137 | classify_pp_end_pad | 0.5 | Proto Prune End Pad |
| 138 | classify_pp_side_pad | 2.5 | Proto Pruner Side Pad |
| 139 | classify_min_slope | 0.414214 | Slope below which lines are called horizontal |
| 140 | classify_max_slope | 2.41421 | Slope above which lines are called vertical |
| 141 | classify_norm_adj_midpoint | 32 | Norm adjust midpoint ... |
| 142 | classify_norm_adj_curl | 2 | Norm adjust curl ... |
| 143 | classify_pico_feature_length | 0.05 | Pico Feature Length |
| 144 | textord_underline_threshold | 0.5 | Fraction of width occupied |
| 145 | edges_childarea | 0.5 | Min area fraction of child outline |
| 146 | edges_boxarea | 0.875 | Min area fraction of grandchild for box |
| 147 | gapmap_big_gaps | 1.75 | xht multiplier |
| 148 | textord_spline_shift_fraction | 0.02 | Fraction of line spacing for quad |
| 149 | textord_skew_ile | 0.5 | Ile of gradients for page skew |
| 150 | textord_skew_lag | 0.02 | Lag for skew on row accumulation |
| 151 | textord_linespace_iqrlimit | 0.2 | Max iqr/median for linespace |
| 152 | textord_width_limit | 8 | Max width of blobs to make rows |
| 153 | textord_chop_width | 1.5 | Max width before chopping |
| 154 | textord_expansion_factor | 1 | Factor to expand rows by in expand_rows |
| 155 | textord_overlap_x | 0.375 | Fraction of linespace for good overlap |
| 156 | textord_minxh | 0.25 | fraction of linesize for min xheight |
| 157 | textord_min_linesize | 1.25 | * blob height for initial linesize |
| 158 | textord_excess_blobsize | 1.3 | New row made if blob makes row this big |
| 159 | textord_occupancy_threshold | 0.4 | Fraction of neighbourhood |
| 160 | textord_underline_width | 2 | Multiple of line_size for underline |
| 161 | textord_min_blob_height_fraction | 0.75 | Min blob height/top to include blob top into xheight stats |
| 162 | textord_xheight_mode_fraction | 0.4 | Min pile height to make xheight |
| 163 | textord_ascheight_mode_fraction | 0.08 | Min pile height to make ascheight |
| 164 | textord_descheight_mode_fraction | 0.08 | Min pile height to make descheight |
| 165 | textord_ascx_ratio_min | 1.25 | Min cap/xheight |
| 166 | textord_ascx_ratio_max | 1.8 | Max cap/xheight |
| 167 | textord_descx_ratio_min | 0.25 | Min desc/xheight |
| 168 | textord_descx_ratio_max | 0.6 | Max desc/xheight |
| 169 | textord_xheight_error_margin | 0.1 | Accepted variation |
| 170 | oldbl_xhfract | 0.4 | Fraction of est allowed in calc |
| 171 | oldbl_dot_error_size | 1.26 | Max aspect ratio of a dot |
| 172 | textord_oldbl_jumplimit | 0.15 | X fraction for new partition |
| 173 | pitsync_joined_edge | 0.75 | Dist inside big blob for chopping |
| 174 | pitsync_offset_freecut_fraction | 0.25 | Fraction of cut for free cuts |
| 175 | textord_tabvector_vertical_gap_fraction | 0.5 | max fraction of mean blob width allowed for vertical gaps in vertical text |
| 176 | textord_tabvector_vertical_box_ratio | 0.5 | Fraction of box matches required to declare a line vertical |
| 177 | textord_projection_scale | 0.2 | Ding rate for mid-cuts |
| 178 | textord_balance_factor | 1 | Ding rate for unbalanced char cells |
| 179 | textord_wordstats_smooth_factor | 0.05 | Smoothing gap stats |
| 180 | textord_words_maxspace | 4 | Multiple of xheight |
| 181 | textord_words_default_maxspace | 3.5 | Max believable third space |
| 182 | textord_words_default_minspace | 0.6 | Fraction of xheight |
| 183 | textord_words_min_minspace | 0.3 | Fraction of xheight |
| 184 | textord_words_default_nonspace | 0.2 | Fraction of xheight |
| 185 | textord_words_initial_lower | 0.25 | Max initial cluster size |
| 186 | textord_words_initial_upper | 0.15 | Min initial cluster spacing |
| 187 | textord_words_minlarge | 0.75 | Fraction of valid gaps needed |
| 188 | textord_words_pitchsd_threshold | 0.04 | Pitch sync threshold |
| 189 | textord_words_def_fixed | 0.016 | Threshold for definite fixed |
| 190 | textord_words_def_prop | 0.09 | Threshold for definite prop |
| 191 | textord_pitch_rowsimilarity | 0.08 | Fraction of xheight for sameness |
| 192 | words_initial_lower | 0.5 | Max initial cluster size |
| 193 | words_initial_upper | 0.15 | Min initial cluster spacing |
| 194 | words_default_prop_nonspace | 0.25 | Fraction of xheight |
| 195 | words_default_fixed_space | 0.75 | Fraction of xheight |
| 196 | words_default_fixed_limit | 0.6 | Allowed size variance |
| 197 | textord_words_definite_spread | 0.3 | Non-fuzzy spacing region |
| 198 | textord_spacesize_ratioprop | 2 | Min ratio space/nonspace |
| 199 | textord_fpiqr_ratio | 1.5 | Pitch IQR/Gap IQR threshold |
| 200 | textord_max_pitch_iqr | 0.2 | Xh fraction noise in pitch |
| 201 | textord_underline_offset | 0.1 | Fraction of x to ignore |
| 202 | ambigs_debug_level | 0 | Debug level for unichar ambiguities |
| 203 | classify_debug_level | 0 | Classify debug level |
| 204 | classify_norm_method | 1 | Normalization Method   ... |
| 205 | matcher_debug_level | 0 | Matcher Debug Level |
| 206 | matcher_debug_flags | 0 | Matcher Debug Flags |
| 207 | classify_learning_debug_level | 0 | Learning Debug Level: |
| 208 | matcher_permanent_classes_min | 1 | Min # of permanent classes |
| 209 | matcher_min_examples_for_prototyping | 3 | Reliable Config Threshold |
| 210 | matcher_sufficient_examples_for_prototyping | 5 | Enable adaption even if the ambiguities have not been seen |
| 211 | classify_adapt_proto_threshold | 230 | Threshold for good protos during adaptive 0-255 |
| 212 | classify_adapt_feature_threshold | 230 | Threshold for good features during adaptive 0-255 |
| 213 | classify_class_pruner_threshold | 229 | Class Pruner Threshold 0-255 |
| 214 | classify_class_pruner_multiplier | 15 | Class Pruner Multiplier 0-255: |
| 215 | classify_cp_cutoff_strength | 7 | Class Pruner CutoffStrength: |
| 216 | classify_integer_matcher_multiplier | 10 | Integer Matcher Multiplier  0-255: |
| 217 | dawg_debug_level | 0 | Set to 1 for general debug info, to 2 for more details, to 3 to see all the debug messages |
| 218 | hyphen_debug_level | 0 | Debug level for hyphenated words. |
| 219 | stopper_smallword_size | 2 | Size of dict word to be treated as non-dict word |
| 220 | stopper_debug_level | 0 | Stopper debug level |
| 221 | tessedit_truncate_wordchoice_log | 10 | Max words to keep in list |
| 222 | max_permuter_attempts | 10000 | Maximum number of different character choices to consider during permutation. This limit is especially useful when user patterns are specified, since overly generic patterns can result in dawg search exploring an overly large number of options. |
| 223 | repair_unchopped_blobs | 1 | Fix blobs that aren't chopped |
| 224 | chop_debug | 0 | Chop debug |
| 225 | chop_split_length | 10000 | Split Length |
| 226 | chop_same_distance | 2 | Same distance |
| 227 | chop_min_outline_points | 6 | Min Number of Points on Outline |
| 228 | chop_seam_pile_size | 150 | Max number of seams in seam_pile |
| 229 | chop_inside_angle | -50 | Min Inside Angle Bend |
| 230 | chop_min_outline_area | 2000 | Min Outline Area |
| 231 | chop_centered_maxwidth | 90 | Width of (smaller) chopped blobs above which we don't care that a chop is not near the center. |
| 232 | chop_x_y_weight | 3 | X / Y  length weight |
| 233 | wordrec_debug_level | 0 | Debug level for wordrec |
| 234 | wordrec_max_join_chunks | 4 | Max number of broken pieces to associate |
| 235 | segsearch_debug_level | 0 | SegSearch debug level |
| 236 | segsearch_max_pain_points | 2000 | Maximum number of pain points stored in the queue |
| 237 | segsearch_max_futile_classifications | 20 | Maximum number of pain point classifications per chunk that did not result in finding a better word choice. |
| 238 | language_model_debug_level | 0 | Language model debug level |
| 239 | language_model_ngram_order | 8 | Maximum order of the character ngram model |
| 240 | language_model_viterbi_list_max_num_prunable | 10 | Maximum number of prunable (those for which PrunablePath() is true) entries in each viterbi list recorded in BLOB_CHOICEs |
| 241 | language_model_viterbi_list_max_size | 500 | Maximum size of viterbi lists recorded in BLOB_CHOICEs |
| 242 | language_model_min_compound_length | 3 | Minimum length of compound words |
| 243 | wordrec_display_segmentations | 0 | Display Segmentations (ScrollView) |
| 244 | tessedit_pageseg_mode | 6 | Page seg mode: 0=osd only, 1=auto+osd, 2=auto_only, 3=auto, 4=column, 5=block_vert, 6=block, 7=line, 8=word, 9=word_circle, 10=char,11=sparse_text, 12=sparse_text+osd, 13=raw_line (Values from PageSegMode enum in tesseract/publictypes.h) |
| 245 | thresholding_method | 0 | Thresholding method: 0 = Otsu, 1 = LeptonicaOtsu, 2 = Sauvola |
| 246 | tessedit_ocr_engine_mode | 3 | Which OCR engine(s) to run (Tesseract, LSTM, both). Defaults to loading and running the most accurate available. |
| 247 | pageseg_devanagari_split_strategy | 0 | Whether to use the top-line splitting process for Devanagari documents while performing page-segmentation. |
| 248 | ocr_devanagari_split_strategy | 0 | Whether to use the top-line splitting process for Devanagari documents while performing ocr. |
| 249 | bidi_debug | 0 | Debug level for BiDi |
| 250 | applybox_debug | 1 | Debug level |
| 251 | applybox_page | 0 | Page number to apply boxes from |
| 252 | tessedit_font_id | 0 | Font ID to use or zero |
| 253 | tessedit_bigram_debug | 0 | Amount of debug output for bigram correction. |
| 254 | debug_noise_removal | 0 | Debug reassignment of small outlines |
| 255 | noise_maxperblob | 8 | Max diacritics to apply to a blob |
| 256 | noise_maxperword | 16 | Max diacritics to apply to a word |
| 257 | debug_x_ht_level | 0 | Reestimate debug |
| 258 | quality_min_initial_alphas_reqd | 2 | alphas in a good word |
| 259 | tessedit_tess_adaption_mode | 39 | Adaptation decision algorithm for tess |
| 260 | multilang_debug_level | 0 | Print multilang debug info. |
| 261 | paragraph_debug_level | 0 | Print paragraph debug info. |
| 262 | tessedit_preserve_min_wd_len | 2 | Only preserve wds longer than this |
| 263 | crunch_rating_max | 10 | For adj length in rating per ch |
| 264 | crunch_pot_indicators | 1 | How many potential indicators needed |
| 265 | crunch_leave_lc_strings | 4 | Don't crunch words with long lower case strings |
| 266 | crunch_leave_uc_strings | 4 | Don't crunch words with long lower case strings |
| 267 | crunch_long_repetitions | 3 | Crunch words with long repetitions |
| 268 | crunch_debug | 0 | As it says |
| 269 | fixsp_non_noise_limit | 1 | How many non-noise blbs either side? |
| 270 | fixsp_done_mode | 1 | What constitutes done for spacing |
| 271 | debug_fix_space_level | 0 | Contextual fixspace debug |
| 272 | x_ht_acceptance_tolerance | 8 | Max allowed deviation of blob top outside of font data |
| 273 | x_ht_min_change | 8 | Min change in xht before actually trying it |
| 274 | superscript_debug | 0 | Debug level for sub & superscript fixer |
| 275 | page_xml_level | 0 | Create the PAGE file on 0=line or 1=word level. |
| 276 | jpg_quality | 85 | Set JPEG quality level |
| 277 | user_defined_dpi | 0 | Specify DPI for input image |
| 278 | min_characters_to_try | 50 | Specify minimum characters to try during OSD |
| 279 | suspect_level | 99 | Suspect marker level |
| 280 | suspect_short_words | 2 | Don't suspect dict wds longer than this |
| 281 | tessedit_reject_mode | 0 | Rejection algorithm |
| 282 | tessedit_image_border | 2 | Rej blbs near image edge limit |
| 283 | min_sane_x_ht_pixels | 8 | Reject any x-ht lt or eq than this |
| 284 | tessedit_page_number | -1 | -1 -> All pages, else specific page to process |
| 285 | tessedit_parallelize | 0 | Run in parallel where possible |
| 286 | lstm_choice_mode | 0 | Allows to include alternative symbols choices in the hOCR output. Valid input values are 0, 1 and 2. 0 is the default value. With 1 the alternative symbol choices per timestep are included. With 2 alternative symbol choices are extracted from the CTC process instead of the lattice. The choices are mapped per character. |
| 287 | lstm_choice_iterations | 5 | Sets the number of cascading iterations for the Beamsearch in lstm_choice_mode. Note that lstm_choice_mode must be set to a value greater than 0 to produce results. |
| 288 | tosp_debug_level | 0 | Debug data |
| 289 | tosp_enough_space_samples_for_median | 3 | or should we use mean |
| 290 | tosp_redo_kern_limit | 10 | No.samples reqd to reestimate for row |
| 291 | tosp_few_samples | 40 | No.gaps reqd with 1 large gap to treat as a table |
| 292 | tosp_short_row | 20 | No.gaps reqd with few cert spaces to use certs |
| 293 | tosp_sanity_method | 1 | How to avoid being silly |
| 294 | textord_max_noise_size | 7 | Pixel size of noise |
| 295 | textord_baseline_debug | 0 | Baseline debug level |
| 296 | textord_noise_sizefraction | 10 | Fraction of size for maxima |
| 297 | textord_noise_translimit | 16 | Transitions for normal blob |
| 298 | textord_noise_sncount | 1 | super norm blobs to save row |
| 299 | use_ambigs_for_adaption | 0 | Use ambigs for deciding whether to adapt to a character |
| 300 | allow_blob_division | 1 | Use divisible blobs chopping |
| 301 | prioritize_division | 0 | Prioritize blob division over chopping |
| 302 | classify_enable_learning | 1 | Enable adaptive classifier |
| 303 | tess_cn_matching | 0 | Character Normalized Matching |
| 304 | tess_bn_matching | 0 | Baseline Normalized Matching |
| 305 | classify_enable_adaptive_matcher | 1 | Enable adaptive classifier |
| 306 | classify_use_pre_adapted_templates | 0 | Use pre-adapted classifier templates |
| 307 | classify_save_adapted_templates | 0 | Save adapted templates to a file |
| 308 | classify_enable_adaptive_debugger | 0 | Enable match debugger |
| 309 | classify_nonlinear_norm | 0 | Non-linear stroke-density normalization |
| 310 | disable_character_fragments | 1 | Do not include character fragments in the results of the classifier |
| 311 | classify_debug_character_fragments | 0 | Bring up graphical debugging windows for fragments training |
| 312 | matcher_debug_separate_windows | 0 | Use two different windows for debugging the matching: One for the protos and one for the features. |
| 313 | classify_bln_numeric_mode | 0 | Assume the input is numbers [0-9]. |
| 314 | load_system_dawg | 1 | Load system word dawg. |
| 315 | load_freq_dawg | 1 | Load frequent word dawg. |
| 316 | load_unambig_dawg | 1 | Load unambiguous word dawg. |
| 317 | load_punc_dawg | 1 | Load dawg with punctuation patterns. |
| 318 | load_number_dawg | 1 | Load dawg with number patterns. |
| 319 | load_bigram_dawg | 1 | Load dawg with special word bigrams. |
| 320 | use_only_first_uft8_step | 0 | Use only the first UTF8 step of the given string when computing log probabilities. |
| 321 | stopper_no_acceptable_choices | 0 | Make AcceptableChoice() always return false. Useful when there is a need to explore all segmentations |
| 322 | segment_nonalphabetic_script | 0 | Don't use any alphabetic-specific tricks. Set to true in the traineddata config file for scripts that are cursive or inherently fixed-pitch |
| 323 | save_doc_words | 0 | Save Document Words |
| 324 | merge_fragments_in_matrix | 1 | Merge the fragments in the ratings matrix and delete them after merging |
| 325 | wordrec_enable_assoc | 1 | Associator Enable |
| 326 | force_word_assoc | 0 | force associator to run regardless of what enable_assoc is. This is used for CJK where component grouping is necessary. |
| 327 | chop_enable | 1 | Chop enable |
| 328 | chop_vertical_creep | 0 | Vertical creep |
| 329 | chop_new_seam_pile | 1 | Use new seam_pile |
| 330 | assume_fixed_pitch_char_segment | 0 | include fixed-pitch heuristics in char segmentation |
| 331 | wordrec_skip_no_truth_words | 0 | Only run OCR for words that had truth recorded in BlamerBundle |
| 332 | wordrec_debug_blamer | 0 | Print blamer debug messages |
| 333 | wordrec_run_blamer | 0 | Try to set the blame for errors |
| 334 | save_alt_choices | 1 | Save alternative paths found during chopping and segmentation search |
| 335 | language_model_ngram_on | 0 | Turn on/off the use of character ngram model |
| 336 | language_model_ngram_use_only_first_uft8_step | 0 | Use only the first UTF8 step of the given string when computing log probabilities. |
| 337 | language_model_ngram_space_delimited_language | 1 | Words are delimited by space |
| 338 | language_model_use_sigmoidal_certainty | 0 | Use sigmoidal score for certainty |
| 339 | tessedit_resegment_from_boxes | 0 | Take segmentation and labeling from box file |
| 340 | tessedit_resegment_from_line_boxes | 0 | Conversion of word/line box file to char box file |
| 341 | tessedit_train_from_boxes | 0 | Generate training data from boxed chars |
| 342 | tessedit_make_boxes_from_boxes | 0 | Generate more boxes from boxed chars |
| 343 | tessedit_train_line_recognizer | 0 | Break input into lines and remap boxes if present |
| 344 | tessedit_dump_pageseg_images | 0 | Dump intermediate images made during page segmentation |
| 345 | tessedit_do_invert | 1 | Try inverted line image if necessary (deprecated, will be removed in release 6, use the 'invert_threshold' parameter instead) |
| 346 | thresholding_debug | 0 | Debug the thresholding process |
| 347 | tessedit_ambigs_training | 0 | Perform training for ambiguities |
| 348 | tessedit_adaption_debug | 0 | Generate and print debug information for adaption |
| 349 | applybox_learn_chars_and_char_frags_mode | 0 | Learn both character fragments (as is done in the special low exposure mode) as well as unfragmented characters. |
| 350 | applybox_learn_ngrams_mode | 0 | Each bounding box is assumed to contain ngrams. Only learn the ngrams whose outlines overlap horizontally. |
| 351 | tessedit_display_outwords | 0 | Draw output words |
| 352 | tessedit_dump_choices | 0 | Dump char choices |
| 353 | tessedit_timing_debug | 0 | Print timing stats |
| 354 | tessedit_fix_fuzzy_spaces | 1 | Try to improve fuzzy spaces |
| 355 | tessedit_unrej_any_wd | 0 | Don't bother with word plausibility |
| 356 | tessedit_fix_hyphens | 1 | Crunch double hyphens? |
| 357 | tessedit_enable_doc_dict | 1 | Add words to the document dictionary |
| 358 | tessedit_debug_fonts | 0 | Output font info per char |
| 359 | tessedit_debug_block_rejection | 0 | Block and Row stats |
| 360 | tessedit_enable_bigram_correction | 1 | Enable correction based on the word bigram dictionary. |
| 361 | tessedit_enable_dict_correction | 0 | Enable single word correction based on the dictionary. |
| 362 | enable_noise_removal | 1 | Remove and conditionally reassign small outlines when they confuse layout analysis, determining diacritics vs noise |
| 363 | tessedit_minimal_rej_pass1 | 0 | Do minimal rejection on pass 1 output |
| 364 | tessedit_test_adaption | 0 | Test adaption criteria |
| 365 | test_pt | 0 | Test for point |
| 366 | paragraph_text_based | 1 | Run paragraph detection on the post-text-recognition (more accurate) |
| 367 | lstm_use_matrix | 1 | Use ratings matrix/beam search with lstm |
| 368 | tessedit_good_quality_unrej | 1 | Reduce rejection on good docs |
| 369 | tessedit_use_reject_spaces | 1 | Reject spaces? |
| 370 | tessedit_preserve_blk_rej_perfect_wds | 1 | Only rej partially rejected words in block rejection |
| 371 | tessedit_preserve_row_rej_perfect_wds | 1 | Only rej partially rejected words in row rejection |
| 372 | tessedit_dont_blkrej_good_wds | 0 | Use word segmentation quality metric |
| 373 | tessedit_dont_rowrej_good_wds | 0 | Use word segmentation quality metric |
| 374 | tessedit_row_rej_good_docs | 1 | Apply row rejection to good docs |
| 375 | tessedit_reject_bad_qual_wds | 1 | Reject all bad quality wds |
| 376 | tessedit_debug_doc_rejection | 0 | Page stats |
| 377 | tessedit_debug_quality_metrics | 0 | Output data to debug file |
| 378 | bland_unrej | 0 | unrej potential with no checks |
| 379 | unlv_tilde_crunching | 0 | Mark v.bad words for tilde crunch |
| 380 | hocr_font_info | 0 | Add font info to hocr output |
| 381 | hocr_char_boxes | 0 | Add coordinates for each character to hocr output |
| 382 | crunch_early_merge_tess_fails | 1 | Before word crunch? |
| 383 | crunch_early_convert_bad_unlv_chs | 0 | Take out ~^ early? |
| 384 | crunch_terrible_garbage | 1 | As it says |
| 385 | crunch_leave_ok_strings | 1 | Don't touch sensible strings |
| 386 | crunch_accept_ok | 1 | Use acceptability in okstring |
| 387 | crunch_leave_accept_strings | 0 | Don't pot crunch sensible strings |
| 388 | crunch_include_numerals | 0 | Fiddle alpha figures |
| 389 | tessedit_prefer_joined_punct | 0 | Reward punctuation joins |
| 390 | tessedit_write_block_separators | 0 | Write block separators in output |
| 391 | tessedit_write_rep_codes | 0 | Write repetition char code |
| 392 | tessedit_write_unlv | 0 | Write .unlv output file |
| 393 | tessedit_create_txt | 0 | Write .txt output file |
| 394 | tessedit_create_hocr | 0 | Write .html hOCR output file |
| 395 | tessedit_create_alto | 0 | Write .xml ALTO file |
| 396 | tessedit_create_page_xml | 0 | Write .page.xml PAGE file |
| 397 | page_xml_polygon | 1 | Create the PAGE file with polygons instead of box values |
| 398 | tessedit_create_lstmbox | 0 | Write .box file for LSTM training |
| 399 | tessedit_create_tsv | 0 | Write .tsv output file |
| 400 | tessedit_create_wordstrbox | 0 | Write WordStr format .box output file |
| 401 | tessedit_create_pdf | 0 | Write .pdf output file |
| 402 | textonly_pdf | 0 | Create PDF with only one invisible text layer |
| 403 | suspect_constrain_1Il | 0 | UNLV keep 1Il chars rejected |
| 404 | tessedit_minimal_rejection | 0 | Only reject tess failures |
| 405 | tessedit_zero_rejection | 0 | Don't reject ANYTHING |
| 406 | tessedit_word_for_word | 0 | Make output have exactly one word per WERD |
| 407 | tessedit_zero_kelvin_rejection | 0 | Don't reject ANYTHING AT ALL |
| 408 | tessedit_rejection_debug | 0 | Adaption debug |
| 409 | tessedit_flip_0O | 1 | Contextual 0O O0 flips |
| 410 | rej_trust_doc_dawg | 0 | Use DOC dawg in 11l conf. detector |
| 411 | rej_1Il_use_dict_word | 0 | Use dictword test |
| 412 | rej_1Il_trust_permuter_type | 1 | Don't double check |
| 413 | rej_use_tess_accepted | 1 | Individual rejection control |
| 414 | rej_use_tess_blanks | 1 | Individual rejection control |
| 415 | rej_use_good_perm | 1 | Individual rejection control |
| 416 | rej_use_sensible_wd | 0 | Extend permuter check |
| 417 | rej_alphas_in_number_perm | 0 | Extend permuter check |
| 418 | tessedit_create_boxfile | 0 | Output text with boxes |
| 419 | tessedit_write_images | 0 | Capture the image from the IPE |
| 420 | interactive_display_mode | 0 | Run interactively? |
| 421 | tessedit_override_permuter | 1 | According to dict_word |
| 422 | tessedit_use_primary_params_model | 0 | In multilingual mode use params model of the primary language |
| 423 | textord_tabfind_show_vlines | 0 | Debug line finding |
| 424 | textord_use_cjk_fp_model | 0 | Use CJK fixed pitch model |
| 425 | poly_allow_detailed_fx | 0 | Allow feature extractors to see the original outline |
| 426 | tessedit_init_config_only | 0 | Only initialize with the config file. Useful if the instance is not going to be used for OCR but say only for layout analysis. |
| 427 | textord_equation_detect | 0 | Turn on equation detector |
| 428 | textord_tabfind_vertical_text | 1 | Enable vertical detection |
| 429 | textord_tabfind_force_vertical_text | 0 | Force using vertical text page mode |
| 430 | preserve_interword_spaces | 0 | Preserve multiple interword spaces |
| 431 | pageseg_apply_music_mask | 0 | Detect music staff and remove intersecting components |
| 432 | textord_single_height_mode | 0 | Script has no xheight, so use a single mode |
| 433 | tosp_old_to_method | 0 | Space stats use prechopping? |
| 434 | tosp_old_to_constrain_sp_kn | 0 | Constrain relative values of inter and intra-word gaps for old_to_method. |
| 435 | tosp_only_use_prop_rows | 1 | Block stats to use fixed pitch rows? |
| 436 | tosp_force_wordbreak_on_punct | 0 | Force word breaks on punct to break long lines in non-space delimited langs |
| 437 | tosp_use_pre_chopping | 0 | Space stats use prechopping? |
| 438 | tosp_old_to_bug_fix | 0 | Fix suspected bug in old code |
| 439 | tosp_block_use_cert_spaces | 1 | Only stat OBVIOUS spaces |
| 440 | tosp_row_use_cert_spaces | 1 | Only stat OBVIOUS spaces |
| 441 | tosp_narrow_blobs_not_cert | 1 | Only stat OBVIOUS spaces |
| 442 | tosp_row_use_cert_spaces1 | 1 | Only stat OBVIOUS spaces |
| 443 | tosp_recovery_isolated_row_stats | 1 | Use row alone when inadequate cert spaces |
| 444 | tosp_only_small_gaps_for_kern | 0 | Better guess |
| 445 | tosp_all_flips_fuzzy | 0 | Pass ANY flip to context? |
| 446 | tosp_fuzzy_limit_all | 1 | Don't restrict kn->sp fuzzy limit to tables |
| 447 | tosp_stats_use_xht_gaps | 1 | Use within xht gap for wd breaks |
| 448 | tosp_use_xht_gaps | 1 | Use within xht gap for wd breaks |
| 449 | tosp_only_use_xht_gaps | 0 | Only use within xht gap for wd breaks |
| 450 | tosp_rule_9_test_punct | 0 | Don't chng kn to space next to punct |
| 451 | tosp_flip_fuzz_kn_to_sp | 1 | Default flip |
| 452 | tosp_flip_fuzz_sp_to_kn | 1 | Default flip |
| 453 | tosp_improve_thresh | 0 | Enable improvement heuristic |
| 454 | textord_no_rejects | 0 | Don't remove noise blobs |
| 455 | textord_show_blobs | 0 | Display unsorted blobs |
| 456 | textord_show_boxes | 0 | Display unsorted blobs |
| 457 | textord_noise_rejwords | 1 | Reject noise-like words |
| 458 | textord_noise_rejrows | 1 | Reject noise-like rows |
| 459 | textord_noise_debug | 0 | Debug row garbage detector |
| 460 | classify_learn_debug_str |  | Class str to debug learning |
| 461 | user_words_file |  | A filename of user-provided words. |
| 462 | user_words_suffix |  | A suffix of user-provided words located in tessdata. |
| 463 | user_patterns_file |  | A filename of user-provided patterns. |
| 464 | user_patterns_suffix |  | A suffix of user-provided patterns located in tessdata. |
| 465 | output_ambig_words_file |  | Output file for ambiguities found in the dictionary |
| 466 | word_to_debug |  | Word for which stopper debug information should be printed to stdout |
| 467 | tessedit_char_blacklist |  | Blacklist of chars not to recognize |
| 468 | tessedit_char_whitelist |  | Whitelist of chars to recognize |
| 469 | tessedit_char_unblacklist |  | List of chars to override tessedit_char_blacklist |
| 470 | tessedit_write_params_to_file |  | Write all parameters to the given file. |
| 471 | applybox_exposure_pattern | .exp | Exposure value follows this pattern in the image filename. The name of the image files are expected to be in the form [lang].[fontname].exp[num].tif |
| 472 | chs_leading_punct | ('`" | Leading punctuation |
| 473 | chs_trailing_punct1 | ).,;:?! | 1st Trailing punctuation |
| 474 | chs_trailing_punct2 | )'`" | 2nd Trailing punctuation |
| 475 | outlines_odd | %|  | Non standard number of outlines |
| 476 | outlines_2 | ij!?%":; | Non standard number of outlines |
| 477 | numeric_punctuation | ., | Punct. chs expected WITHIN numbers |
| 478 | unrecognised_char | | | Output char for unidentified blobs |
| 479 | ok_repeated_ch_non_alphanum_wds | -?*= | Allow NN to unrej |
| 480 | conflict_set_I_l_1 | Il1[] | Il1 conflict set |
| 481 | file_type | .tif | Filename extension |
| 482 | tessedit_load_sublangs |  | List of languages to load with this one |
| 483 | page_separator |  | Page separator (default is form feed control character) |
| 484 | classify_char_norm_range | 0.2 | Character Normalization Range ... |
| 485 | classify_max_rating_ratio | 1.5 | Veto ratio between classifier ratings |
| 486 | classify_max_certainty_margin | 5.5 | Veto difference between classifier certainties |
| 487 | matcher_good_threshold | 0.125 | Good Match (0-1) |
| 488 | matcher_reliable_adaptive_result | 0 | Great Match (0-1) |
| 489 | matcher_perfect_threshold | 0.02 | Perfect Match (0-1) |
| 490 | matcher_bad_match_pad | 0.15 | Bad Match Pad (0-1) |
| 491 | matcher_rating_margin | 0.1 | New template margin (0-1) |
| 492 | matcher_avg_noise_size | 12 | Avg. noise blob length |
| 493 | matcher_clustering_max_angle_delta | 0.015 | Maximum angle delta for prototype clustering |
| 494 | classify_misfit_junk_penalty | 0 | Penalty to apply when a non-alnum is vertically out of its expected textline position |
| 495 | rating_scale | 1.5 | Rating scaling factor |
| 496 | tessedit_class_miss_scale | 0.00390625 | Scale factor for features not used |
| 497 | classify_adapted_pruning_factor | 2.5 | Prune poor adapted results this much worse than best result |
| 498 | classify_adapted_pruning_threshold | -1 | Threshold at which classify_adapted_pruning_factor starts |
| 499 | classify_character_fragments_garbage_certainty_threshold | -3 | Exclude fragments that do not look like whole characters from training and adaption |
| 500 | speckle_large_max_size | 0.3 | Max large speckle size |
| 501 | speckle_rating_penalty | 10 | Penalty to add to worst rating for noise |
| 502 | xheight_penalty_subscripts | 0.125 | Score penalty (0.1 = 10%) added if there are subscripts or superscripts in a word, but it is otherwise OK. |
| 503 | xheight_penalty_inconsistent | 0.25 | Score penalty (0.1 = 10%) added if an xheight is inconsistent. |
| 504 | segment_penalty_dict_frequent_word | 1 | Score multiplier for word matches which have good case and are frequent in the given language (lower is better). |
| 505 | segment_penalty_dict_case_ok | 1.1 | Score multiplier for word matches that have good case (lower is better). |
| 506 | segment_penalty_dict_case_bad | 1.3125 | Default score multiplier for word matches, which may have case issues (lower is better). |
| 507 | segment_penalty_dict_nonword | 1.25 | Score multiplier for glyph fragment segmentations which do not match a dictionary word (lower is better). |
| 508 | segment_penalty_garbage | 1.5 | Score multiplier for poorly cased strings that are not in the dictionary and generally look like garbage (lower is better). |
| 509 | certainty_scale | 20 | Certainty scaling factor |
| 510 | stopper_nondict_certainty_base | -2.5 | Certainty threshold for non-dict words |
| 511 | stopper_phase2_certainty_rejection_offset | 1 | Reject certainty offset |
| 512 | stopper_certainty_per_char | -0.5 | Certainty to add for each dict char above small word size. |
| 513 | stopper_allowable_character_badness | 3 | Max certainty variation allowed in a word (in sigma) |
| 514 | doc_dict_pending_threshold | 0 | Worst certainty for using pending dictionary |
| 515 | doc_dict_certainty_threshold | -2.25 | Worst certainty for words that can be inserted into the document dictionary |
| 516 | tessedit_certainty_threshold | -2.25 | Good blob limit |
| 517 | chop_split_dist_knob | 0.5 | Split length adjustment |
| 518 | chop_overlap_knob | 0.9 | Split overlap adjustment |
| 519 | chop_center_knob | 0.15 | Split center adjustment |
| 520 | chop_sharpness_knob | 0.06 | Split sharpness adjustment |
| 521 | chop_width_change_knob | 5 | Width change adjustment |
| 522 | chop_ok_split | 100 | OK split limit |
| 523 | chop_good_split | 50 | Good split limit |
| 524 | segsearch_max_char_wh_ratio | 2 | Maximum character width-to-height ratio |
| 525 | language_model_ngram_small_prob | 1e-06 | To avoid overly small denominators use this as the floor of the probability returned by the ngram model. |
| 526 | language_model_ngram_nonmatch_score | -40 | Average classifier score of a non-matching unichar. |
| 527 | language_model_ngram_scale_factor | 0.03 | Strength of the character ngram model relative to the character classifier |
| 528 | language_model_ngram_rating_factor | 16 | Factor to bring log-probs into the same range as ratings when multiplied by outline length |
| 529 | language_model_penalty_non_freq_dict_word | 0.1 | Penalty for words not in the frequent word dictionary |
| 530 | language_model_penalty_non_dict_word | 0.15 | Penalty for non-dictionary words |
| 531 | language_model_penalty_punc | 0.2 | Penalty for inconsistent punctuation |
| 532 | language_model_penalty_case | 0.1 | Penalty for inconsistent case |
| 533 | language_model_penalty_script | 0.5 | Penalty for inconsistent script |
| 534 | language_model_penalty_chartype | 0.3 | Penalty for inconsistent character type |
| 535 | language_model_penalty_font | 0 | Penalty for inconsistent font |
| 536 | language_model_penalty_spacing | 0.05 | Penalty for inconsistent spacing |
| 537 | language_model_penalty_increment | 0.01 | Penalty increment |
| 538 | invert_threshold | 0.7 | For lines with a mean confidence below this value, OCR is also tried with an inverted image |
| 539 | thresholding_window_size | 0.33 | Window size for measuring local statistics (to be multiplied by image DPI). This parameter is used by the Sauvola thresholding method |
| 540 | thresholding_kfactor | 0.34 | Factor for reducing threshold due to variance. This parameter is used by the Sauvola thresholding method. Normal range: 0.2-0.5 |
| 541 | thresholding_tile_size | 0.33 | Desired tile size (to be multiplied by image DPI). This parameter is used by the LeptonicaOtsu thresholding method |
| 542 | thresholding_smooth_kernel_size | 0 | Size of convolution kernel applied to threshold array (to be multiplied by image DPI). Use 0 for no smoothing. This parameter is used by the LeptonicaOtsu thresholding method |
| 543 | thresholding_score_fraction | 0.1 | Fraction of the max Otsu score. This parameter is used by the LeptonicaOtsu thresholding method. For standard Otsu use 0.0, otherwise 0.1 is recommended |
| 544 | noise_cert_basechar | -8 | Hingepoint for base char certainty |
| 545 | noise_cert_disjoint | -1 | Hingepoint for disjoint certainty |
| 546 | noise_cert_punc | -3 | Threshold for new punc char certainty |
| 547 | noise_cert_factor | 0.375 | Scaling on certainty diff from Hingepoint |
| 548 | quality_rej_pc | 0.08 | good_quality_doc lte rejection limit |
| 549 | quality_blob_pc | 0 | good_quality_doc gte good blobs limit |
| 550 | quality_outline_pc | 1 | good_quality_doc lte outline error limit |
| 551 | quality_char_pc | 0.95 | good_quality_doc gte good char limit |
| 552 | test_pt_x | 100000 | xcoord |
| 553 | test_pt_y | 100000 | ycoord |
| 554 | tessedit_reject_doc_percent | 65 | %rej allowed before rej whole doc |
| 555 | tessedit_reject_block_percent | 45 | %rej allowed before rej whole block |
| 556 | tessedit_reject_row_percent | 40 | %rej allowed before rej whole row |
| 557 | tessedit_whole_wd_rej_row_percent | 70 | Number of row rejects in whole word rejects which prevents whole row rejection |
| 558 | tessedit_good_doc_still_rowrej_wd | 1.1 | rej good doc wd if more than this fraction rejected |
| 559 | quality_rowrej_pc | 1.1 | good_quality_doc gte good char limit |
| 560 | crunch_terrible_rating | 80 | crunch rating lt this |
| 561 | crunch_poor_garbage_cert | -9 | crunch garbage cert lt this |
| 562 | crunch_poor_garbage_rate | 60 | crunch garbage rating lt this |
| 563 | crunch_pot_poor_rate | 40 | POTENTIAL crunch rating lt this |
| 564 | crunch_pot_poor_cert | -8 | POTENTIAL crunch cert lt this |
| 565 | crunch_del_rating | 60 | POTENTIAL crunch rating lt this |
| 566 | crunch_del_cert | -10 | POTENTIAL crunch cert lt this |
| 567 | crunch_del_min_ht | 0.7 | Del if word ht lt xht x this |
| 568 | crunch_del_max_ht | 3 | Del if word ht gt xht x this |
| 569 | crunch_del_min_width | 3 | Del if word width lt xht x this |
| 570 | crunch_del_high_word | 1.5 | Del if word gt xht x this above bl |
| 571 | crunch_del_low_word | 0.5 | Del if word gt xht x this below bl |
| 572 | crunch_small_outlines_size | 0.6 | Small if lt xht x this |
| 573 | fixsp_small_outlines_size | 0.28 | Small if lt xht x this |
| 574 | superscript_worse_certainty | 2 | How many times worse certainty does a superscript position glyph need to be for us to try classifying it as a char with a different baseline? |
| 575 | superscript_bettered_certainty | 0.97 | What reduction in badness do we think sufficient to choose a superscript over what we'd thought.  For example, a value of 0.6 means we want to reduce badness of certainty by at least 40% |
| 576 | superscript_scaledown_ratio | 0.4 | A superscript scaled down more than this is unbelievably small.  For example, 0.3 means we expect the font size to be no smaller than 30% of the text line font size. |
| 577 | subscript_max_y_top | 0.5 | Maximum top of a character measured as a multiple of x-height above the baseline for us to reconsider whether it's a subscript. |
| 578 | superscript_min_y_bottom | 0.3 | Minimum bottom of a character measured as a multiple of x-height above the baseline for us to reconsider whether it's a superscript. |
| 579 | suspect_rating_per_ch | 999.9 | Don't touch bad rating limit |
| 580 | suspect_accept_rating | -999.9 | Accept good rating limit |
| 581 | tessedit_lower_flip_hyphen | 1.5 | Aspect ratio dot/hyphen test |
| 582 | tessedit_upper_flip_hyphen | 1.8 | Aspect ratio dot/hyphen test |
| 583 | rej_whole_of_mostly_reject_word_fract | 0.85 | if >this fract |
| 584 | min_orientation_margin | 7 | Min acceptable orientation margin |
| 585 | textord_tabfind_vertical_text_ratio | 0.5 | Fraction of textlines deemed vertical to use vertical page mode |
| 586 | textord_tabfind_aligned_gap_fraction | 0.75 | Fraction of height used as a minimum gap for aligned blobs. |
| 587 | lstm_rating_coefficient | 5 | Sets the rating coefficient for the lstm choices. The smaller the coefficient, the better are the ratings for each choice and less information is lost due to the cut off at 0. The standard value is 5 |
| 588 | tosp_old_sp_kn_th_factor | 2 | Factor for defining space threshold in terms of space and kern sizes |
| 589 | tosp_threshold_bias1 | 0 | how far between kern and space? |
| 590 | tosp_threshold_bias2 | 0 | how far between kern and space? |
| 591 | tosp_narrow_fraction | 0.3 | Fract of xheight for narrow |
| 592 | tosp_narrow_aspect_ratio | 0.48 | narrow if w/h less than this |
| 593 | tosp_wide_fraction | 0.52 | Fract of xheight for wide |
| 594 | tosp_wide_aspect_ratio | 0 | wide if w/h less than this |
| 595 | tosp_fuzzy_space_factor | 0.6 | Fract of xheight for fuzz sp |
| 596 | tosp_fuzzy_space_factor1 | 0.5 | Fract of xheight for fuzz sp |
| 597 | tosp_fuzzy_space_factor2 | 0.72 | Fract of xheight for fuzz sp |
| 598 | tosp_gap_factor | 0.83 | gap ratio to flip sp->kern |
| 599 | tosp_kern_gap_factor1 | 2 | gap ratio to flip kern->sp |
| 600 | tosp_kern_gap_factor2 | 1.3 | gap ratio to flip kern->sp |
| 601 | tosp_kern_gap_factor3 | 2.5 | gap ratio to flip kern->sp |
| 602 | tosp_ignore_big_gaps | -1 | xht multiplier |
| 603 | tosp_ignore_very_big_gaps | 3.5 | xht multiplier |
| 604 | tosp_rep_space | 1.6 | rep gap multiplier for space |
| 605 | tosp_enough_small_gaps | 0.65 | Fract of kerns reqd for isolated row stats |
| 606 | tosp_table_kn_sp_ratio | 2.25 | Min difference of kn & sp in table |
| 607 | tosp_table_xht_sp_ratio | 0.33 | Expect spaces bigger than this |
| 608 | tosp_table_fuzzy_kn_sp_ratio | 3 | Fuzzy if less than this |
| 609 | tosp_fuzzy_kn_fraction | 0.5 | New fuzzy kn alg |
| 610 | tosp_fuzzy_sp_fraction | 0.5 | New fuzzy sp alg |
| 611 | tosp_min_sane_kn_sp | 1.5 | Don't trust spaces less than this time kn |
| 612 | tosp_init_guess_kn_mult | 2.2 | Thresh guess - mult kn by this |
| 613 | tosp_init_guess_xht_mult | 0.28 | Thresh guess - mult xht by this |
| 614 | tosp_max_sane_kn_thresh | 5 | Multiplier on kn to limit thresh |
| 615 | tosp_flip_caution | 0 | Don't autoflip kn to sp when large separation |
| 616 | tosp_large_kerning | 0.19 | Limit use of xht gap with large kns |
| 617 | tosp_dont_fool_with_small_kerns | -1 | Limit use of xht gap with odd small kns |
| 618 | tosp_near_lh_edge | 0 | Don't reduce box if the top left is non blank |
| 619 | tosp_silly_kn_sp_gap | 0.2 | Don't let sp minus kn get too small |
| 620 | tosp_pass_wide_fuzz_sp_to_context | 0.75 | How wide fuzzies need context |
| 621 | textord_noise_area_ratio | 0.7 | Fraction of bounding box for noise |
| 622 | textord_initialx_ile | 0.75 | Ile of sizes for xheight guess |
| 623 | textord_initialasc_ile | 0.9 | Ile of sizes for xheight guess |
| 624 | textord_noise_sizelimit | 0.5 | Fraction of x for big t count |
| 625 | textord_noise_normratio | 2 | Dot to norm ratio for deletion |
| 626 | textord_noise_syfract | 0.2 | xh fract height error for norm blobs |
| 627 | textord_noise_sxfract | 0.4 | xh fract width error for norm blobs |
| 628 | textord_noise_hfract | 0.015625 | Height fraction to discard outlines as speckle noise |
| 629 | textord_noise_rowratio | 6 | Dot to norm ratio for deletion |
| 630 | textord_blshift_maxshift | 0 | Max baseline shift |
| 631 | textord_blshift_xfraction | 9.99 | Min size of baseline shift |
