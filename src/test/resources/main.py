import urllib

import requests
import os
from bs4 import BeautifulSoup


def download(url):
    headers = requests.utils.default_headers()
    headers.update({
        'User-Agent': 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/52.0',
    })

    link = requests.get(
        url,
        headers=headers)
    content = BeautifulSoup(link.content, "html.parser")
    return content


def save(file, name):
    with open('{0}'.format(name), 'w') as f:
        print("saving " + name)
        f.write(str(file))


def infer_links(main_page):
    print("inferring links")
    list_links = []
    for div in main_page.find_all('div', class_='b-content__inline_item-link'):
        for a in div.find_all('a'):
            list_links.append(a['href'])
    return list_links


if __name__ == '__main__':
    url = "https://hdrezka.website/page/{0}/?filter=last&genre=1"
    end_page = 6
    properties = {}

    for i in range(1, end_page):
        main_page = download(url.format(i))
        os.makedirs(str(i))
        save(main_page, str(i) + '/main_page.html')
        links = infer_links(main_page)
        file_counter = 1
        for link in links:
            file = download(link)
            composed_name = str(i) + '/film' + str(file_counter) + '.html'
            save(file, composed_name)
            properties[link] = composed_name
            file_counter = file_counter + 1
        file_counter = 1

    text = ''
    for key in properties:
        text += urllib.parse.quote(key, safe='') + '=' + properties[key] + '\n'
    save(text, "webmap.properties")
