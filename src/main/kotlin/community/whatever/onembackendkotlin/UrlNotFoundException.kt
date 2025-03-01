package community.whatever.onembackendkotlin

const val URL_NOT_FOUND_MESSAGE = "해당 키에 대한 URL이 존재하지 않습니다."

class UrlNotFoundException : RuntimeException(URL_NOT_FOUND_MESSAGE)
