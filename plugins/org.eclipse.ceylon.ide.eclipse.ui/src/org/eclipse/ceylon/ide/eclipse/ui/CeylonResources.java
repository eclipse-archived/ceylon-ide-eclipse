/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.ui;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public interface CeylonResources {
    
    public static final String CEYLON_FILE = "ceylon_file";
    public static final String CEYLON_MODULE_DESC = "ceylon_module_desc";
    public static final String CEYLON_PACKAGE_DESC = "ceylon_package_desc";
    public static final String CEYLON_FOLDER = "ceylon_folder";
    public static final String CEYLON_SOURCE_FOLDER = "ceylon_source_folder";
    public static final String CEYLON_FILE_WARNING = "ceylon_file_warning";
    public static final String CEYLON_FILE_ERROR = "ceylon_file_error";
    public static final String JAVA_FILE = "java_file";
    public static final String JAVA_CLASS_FILE = "java_class_file";
    public static final String GENERIC_FILE = "generic_file";
    
    public static final String CEYLON_ALIAS = "ceylon_alias";
    public static final String CEYLON_OBJECT = "ceylon_object";
    public static final String CEYLON_LOCAL_OBJECT = "ceylon_local_object";
    public static final String CEYLON_CONSTRUCTOR = "ceylon_constructor";
    public static final String CEYLON_CLASS = "ceylon_class";
    public static final String CEYLON_INTERFACE = "ceylon_interface";
    public static final String CEYLON_LOCAL_CLASS = "ceylon_local_class";
    public static final String CEYLON_LOCAL_INTERFACE = "ceylon_local_interface";
    public static final String CEYLON_METHOD = "ceylon_method";
    public static final String CEYLON_ATTRIBUTE = "ceylon_attribute";
    public static final String CEYLON_LOCAL_METHOD = "ceylon_local_method";
    public static final String CEYLON_LOCAL_ATTRIBUTE = "ceylon_local_attribute";
    public static final String CEYLON_PARAMETER = "ceylon_parameter";
    public static final String CEYLON_PARAMETER_METHOD = "ceylon_parameter_method";
    public static final String CEYLON_TYPE_PARAMETER = "ceylon_type_parameter";
    public static final String CEYLON_FUN = "ceylon_fun";
    public static final String CEYLON_LOCAL_FUN = "ceylon_local_fun";
    
    public static final String CEYLON_MODULE = "ceylon_module";
    public static final String CEYLON_SOURCE_ARCHIVE = "ceylon_source_archive";
    public static final String CEYLON_BINARY_ARCHIVE = "ceylon_binary_archive";
    public static final String CEYLON_PACKAGE = "ceylon_package";
    public static final String CEYLON_IMPORT = "ceylon_import";
    public static final String CEYLON_IMPORT_LIST = "ceylon_import_list";
    
    public static final String CEYLON_PROJECT = "ceylon_project";
    
    public static final String CEYLON_ARGUMENT = "ceylon_argument";
    public static final String CEYLON_DEFAULT_REFINEMENT = "ceylon_default_refinement";
    public static final String CEYLON_FORMAL_REFINEMENT = "ceylon_formal_refinement";
    
    public static final String CEYLON_OPEN_DECLARATION = "ceylon_open_declaration";
    
    public static final String CEYLON_SEARCH_RESULTS = "search_results";
    
    public static final String CEYLON_CORRECTION = "correction";
    public static final String CEYLON_CHANGE = "change";
    public static final String CEYLON_COMPOSITE_CHANGE = "composite_change";
    public static final String CEYLON_RENAME = "rename";
    public static final String CEYLON_MOVE = "move";
    public static final String CEYLON_ADD = "add";
    public static final String CEYLON_DELETE = "delete";
    public static final String CEYLON_REORDER = "reorder";
    public static final String CEYLON_REVEAL = "reveal";
    public static final String CEYLON_ADD_CORRECTION = "add_correction";
    public static final String CEYLON_REMOVE_CORRECTION = "remove_correction";
    public static final String CEYLON_DELETE_IMPORT = "delete_import";
    public static final String CEYLON_SUPPRESS_WARNINGS = "suppress_warnings";
    
    public static final String CEYLON_NEW_PROJECT = "ceylon_new_project";
    public static final String CEYLON_NEW_FILE = "ceylon_new_file";
    public static final String CEYLON_NEW_MODULE = "ceylon_new_module";
    public static final String CEYLON_NEW_PACKAGE = "ceylon_new_package";
    public static final String CEYLON_NEW_FOLDER = "ceylon_new_folder";
    public static final String CEYLON_EXPORT_CAR = "ceylon_export_car";
    public static final String CEYLON_EXPORT_JAR = "ceylon_export_jar";
    
    public static final String CEYLON_DECS = "ceylon_decs";
    public static final String CEYLON_REFS = "ceylon_refs";
    
    public static final String CEYLON_HIER = "ceylon_hier";
    public static final String CEYLON_SUP = "ceylon_sup";
    public static final String CEYLON_SUB = "ceylon_sub";
    public static final String CEYLON_INHERITED = "ceylon_inherited";
    
    public static final String CEYLON_ERR = "ceylon_err";
    public static final String CEYLON_WARN = "ceylon_warn";
    
    public static final String CONFIG_ANN_DIS = "config_ann_dis";
    public static final String CONFIG_ANN = "config_ann";
    public static final String CONFIG_WARNINGS = "config_warnings";
    public static final String CONFIG_LABELS = "config_labels";
    public static final String GOTO = "goto";
    public static final String HIERARCHY = "hierarchy";
    
    public static final String CEYLON_SOURCE = "source";
    public static final String CEYLON_OUTLINE = "outline";
    public static final String CEYLON_HIERARCHY = "hierarchy";
    
    public static final String ELE32 ="ceylon_ele32";
    
    public static final String SHIFT_LEFT = "shift_l";
    public static final String SHIFT_RIGHT = "shift_r";
    public static final String QUICK_ASSIST = "quickfix";
    
    public static final String BUILDER = "builder";
    public static final String MODULE_VERSION = "module_version";
    public static final String HIDE_PRIVATE = "hide_private";
    public static final String EXPAND_ALL = "expandall";
    public static final String PAGING = "paging";
    public static final String SHOW_DOC = "show_doc";
    public static final String REPOSITORIES = "repositories";
    public static final String RUNTIME_OBJ = "runtime_obj";
    public static final String MULTIPLE_TYPES = "ceylon_types";
    
    public static final String CEYLON_LOCAL_NAME = "local_name";
    
    public static final String CEYLON_ERROR = "ceylon_error";
    public static final String CEYLON_WARNING = "ceylon_warning";
    
    public static final String PROJECT_MODE = "project_mode";
    public static final String FOLDER_MODE = "folder_mode";
    public static final String PACKAGE_MODE = "package_mode";
    public static final String MODULE_MODE = "module_mode";
    public static final String UNIT_MODE = "unit_mode";
    public static final String TYPE_MODE = "type_mode";
    
    public static final String FLAT_MODE = "flat_mode";
    public static final String TREE_MODE = "tree_mode";
    
    public static final String TERMINATE_STATEMENT = "terminate_statement";
    public static final String FORMAT_BLOCK = "format_block";
    public static final String ADD_COMMENT = "add_comment";
    public static final String REMOVE_COMMENT = "remove_comment";
    public static final String TOGGLE_COMMENT = "toggle_comment";
    public static final String CORRECT_INDENT = "correct_indent";
    public static final String LAST_EDIT = "last_edit";
    public static final String NEXT_ANN = "next_ann";
    public static final String PREV_ANN = "prev_ann";
    
    public static final String CEYLON_SEARCH = "ceylon_search";
    
    public static final String HISTORY = "history";
    
    public static final String SORT_ALPHA = "sort_alpha";
    
    public static final String CEYLON_LITERAL = "literal";
    
    public static final ImageRegistry imageRegistry = 
            CeylonPlugin.imageRegistry();
    
    public static final Image FILE = imageRegistry.get(CEYLON_FILE);
    public static final Image FOLDER = imageRegistry.get(CEYLON_FOLDER);
    public static final Image ALIAS = imageRegistry.get(CEYLON_ALIAS);
    public static final Image CLASS = imageRegistry.get(CEYLON_CLASS);
    public static final Image INTERFACE = imageRegistry.get(CEYLON_INTERFACE);
    public static final Image LOCAL_CLASS = imageRegistry.get(CEYLON_LOCAL_CLASS);
    public static final Image LOCAL_INTERFACE = imageRegistry.get(CEYLON_LOCAL_INTERFACE);
    public static final Image METHOD = imageRegistry.get(CEYLON_METHOD);
    public static final Image ATTRIBUTE = imageRegistry.get(CEYLON_ATTRIBUTE);
    public static final Image LOCAL_METHOD = imageRegistry.get(CEYLON_LOCAL_METHOD);
    public static final Image LOCAL_ATTRIBUTE = imageRegistry.get(CEYLON_LOCAL_ATTRIBUTE);
    public static final Image PARAMETER = imageRegistry.get(CEYLON_PARAMETER);
    public static final Image PARAMETER_METHOD = imageRegistry.get(CEYLON_PARAMETER_METHOD);
    public static final Image PACKAGE = imageRegistry.get(CEYLON_PACKAGE);
    public static final Image MODULE = imageRegistry.get(CEYLON_MODULE);
    public static final Image SOURCE_FOLDER = imageRegistry.get(CEYLON_SOURCE_FOLDER);
    public static final Image SOURCE_ARCHIVE = imageRegistry.get(CEYLON_SOURCE_ARCHIVE);
    public static final Image BINARY_ARCHIVE = imageRegistry.get(CEYLON_BINARY_ARCHIVE);
    public static final Image VERSION = imageRegistry.get(MODULE_VERSION);
    public static final Image IMPORT = imageRegistry.get(CEYLON_IMPORT);
    public static final Image IMPORT_LIST = imageRegistry.get(CEYLON_IMPORT_LIST);
    public static final Image PROJECT = imageRegistry.get(CEYLON_PROJECT);
    public static final Image MINOR_CHANGE = imageRegistry.get(CEYLON_CORRECTION);
    public static final Image SUPPRESS_WARNING = imageRegistry.get(CEYLON_SUPPRESS_WARNINGS);
    public static final Image CONFIG_WARNING = imageRegistry.get(CONFIG_WARNINGS);
    public static final Image CHANGE = imageRegistry.get(CEYLON_CHANGE);
    public static final Image COMPOSITE_CHANGE = imageRegistry.get(CEYLON_COMPOSITE_CHANGE);
    public static final Image RENAME = imageRegistry.get(CEYLON_RENAME);
    public static final Image REORDER = imageRegistry.get(CEYLON_REORDER);
    public static final Image REVEAL = imageRegistry.get(CEYLON_REVEAL);
    public static final Image MOVE = imageRegistry.get(CEYLON_MOVE);
    public static final Image ADD = imageRegistry.get(CEYLON_ADD);
    public static final Image ADD_CORR = imageRegistry.get(CEYLON_ADD_CORRECTION);
    public static final Image REMOVE_CORR = imageRegistry.get(CEYLON_REMOVE_CORRECTION);
    public static final Image LOCAL_NAME = imageRegistry.get(CEYLON_LOCAL_NAME);
    public static final Image MULTIPLE_TYPES_IMAGE = imageRegistry.get(MULTIPLE_TYPES);
    public static final Image REPO = imageRegistry.get(RUNTIME_OBJ);
    
    public static final Image ERROR = imageRegistry.get(CEYLON_ERROR);
    public static final Image WARNING = imageRegistry.get(CEYLON_WARNING);

    //decorations
    public static final String WARNING_IMAGE = "warning_co.png";
    public static final String ERROR_IMAGE = "error_co.png";
    public static final String REFINES_IMAGE = "over_tiny_co.png";
    public static final String IMPLEMENTS_IMAGE = "implm_tiny_co.png";
    public static final String FINAL_IMAGE = "final_co.png";
    public static final String SEALED_IMAGE = "static_co.png";
    public static final String FORMAL_IMAGE = "formal_co.png";
    public static final String DEFAULT_IMAGE = "default_co.png";
    public static final String ABSTRACT_IMAGE = "abstract_co.png";
    public static final String NATIVE_IMAGE = "native_co.png";
    public static final String VARIABLE_IMAGE = "volatile_co.png";
    public static final String ANNOTATION_IMAGE = "annotation_tsk.png";
    public static final String ENUM_IMAGE = "enum_tsk.png";
    public static final String ALIAS_IMAGE = "linked_co.gif";
    public static final String DEPRECATED_IMAGE = "deprecated.png";
    public static final String FOCUS_IMAGE = "focus_ovr.png";
    public static final String RUN_IMAGE = "run_co.png";

}