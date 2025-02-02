library(tidyverse)
library(rvest)
library(httr)
library(dplyr)

channel_data = read_csv("C:/Users/user/OneDrive/바탕 화면/프로젝트/YouTube-View-History/datasets/YouTube_Channels_COPY.csv", locale = locale(encoding = "EUC-KR"))
channel_data
extract_channel_id <- function(url) {
  # 정규 표현식을 이용하여 채널 ID 추출
  channel_id <- str_extract(url, "UC[\\w-]{22}")
  return(channel_id)
}

get_subscriber <- function(url) {
  # 유튜브 채널 페이지 URL 생성
  #url <- paste0("https://www.youtube.com/channel/", channel_id)
  
  # 페이지 가져오기
  page <- read_html(url)
  
  # 구독자 수 추출 (XPath는 유튜브 페이지 구조에 따라 변경될 수 있습니다.)
  subscriber <- page %>% 
    html_nodes(".yt-stat-count") %>% 
    html_text() %>% 
    .[1] %>% 
    str_replace_all(",", "") %>% 
    as.numeric()
  
  return(subscriber)
}

# 채널 링크를 이용하여 채널 ID 추출
#channel_data$channel_id = sapply(channel_data$channel_link, extract_channel_id)

channel_data = channel_data %>% mutate(subscriber = sapply(sapply(channel_data$channel_link, get_subscriber))
# 채널 ID를 이용하여 구독자 수 추출
for (i in 1:length(channel_data$channel_name)) {
  channel_data$subscriber[i] <- get_subscriber(channel_data$channel_
}


# 데이터 프레임 출력
channel_data$subscriber = sapply(channel_data$channel_link, get_subscriber)
channel_data



