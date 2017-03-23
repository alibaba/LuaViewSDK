//
//  JUFLXLayoutDefine.h
//  LuaViewSDK
//
//  Fork from https://github.com/google/gdata-objectivec-client/blob/master/Source%2FGDataTargetNamespace.h
//  Created by xiekw on 16/3/31.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#if defined(JUFLX_LAYOUT_NAMESPACE)

    #define _JUFLX_NS_SYMBOL_INNER(namespace, symbol) namespace ## _ ## symbol
    #define _JUFLX_NS_SYMBOL_MIDDLE(namespace, symbol) _JUFLX_NS_SYMBOL_INNER(namespace, symbol)
    #define _JUFLX_NS_SYMBOL(symbol) _JUFLX_NS_SYMBOL_MIDDLE(JUFLX_LAYOUT_NAMESPACE, symbol)

    #define _JUFLX_NS_STRING_INNER(namespace) #namespace
    #define _JUFLX_NS_STRING_MIDDLE(namespace) _JUFLX_NS_STRING_INNER(namespace)
    #define JUFLX_LAYOUT_NAMESPACE_STRING _JUFLX_NS_STRING_MIDDLE(JUFLX_LAYOUT_NAMESPACE)

    #define css_direction_t                _JUFLX_NS_SYMBOL(css_direction_t)
    #define css_flex_direction_t           _JUFLX_NS_SYMBOL(css_flex_direction_t)
    #define css_justify_t                  _JUFLX_NS_SYMBOL(css_justify_t)
    #define css_align_t                    _JUFLX_NS_SYMBOL(css_align_t)
    #define css_position_type_t            _JUFLX_NS_SYMBOL(css_position_type_t)
    #define css_wrap_type_t                _JUFLX_NS_SYMBOL(css_wrap_type_t)
    #define css_position_t                 _JUFLX_NS_SYMBOL(css_position_t)
    #define css_dimension_t                _JUFLX_NS_SYMBOL(css_dimension_t)
    #define css_layout_t                   _JUFLX_NS_SYMBOL(css_layout_t)
    #define css_dim_t                      _JUFLX_NS_SYMBOL(css_dim_t)
    #define css_style_t                    _JUFLX_NS_SYMBOL(css_style_t)
    #define css_node_t                     _JUFLX_NS_SYMBOL(css_node_t)
    #define new_css_node                   _JUFLX_NS_SYMBOL(new_css_node)
    #define init_css_node                  _JUFLX_NS_SYMBOL(init_css_node)
    #define free_css_node                  _JUFLX_NS_SYMBOL(free_css_node)
    #define css_print_options_t            _JUFLX_NS_SYMBOL(css_print_options_t)
    #define print_css_node                 _JUFLX_NS_SYMBOL(print_css_node)
    #define layoutNode                     _JUFLX_NS_SYMBOL(layoutNode)
    #define isUndefined                    _JUFLX_NS_SYMBOL(isUndefined)

#endif
