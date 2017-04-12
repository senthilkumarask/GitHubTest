package com.bbb.framework.httpquery.parser;

import com.bbb.framework.httpquery.vo.HTTPServiceResponseIF;

public interface ResultParserIF {

	HTTPServiceResponseIF parse(String responseObject);
}
