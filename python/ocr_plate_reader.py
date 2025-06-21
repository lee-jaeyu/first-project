import easyocr
import re
import sys
import os
import cv2
import warnings
import numpy as np

warnings.filterwarnings("ignore", category=UserWarning)

def imread_unicode(path):
    try:
        with open(path, 'rb') as f:
            bytes_data = np.asarray(bytearray(f.read()), dtype=np.uint8)
            image = cv2.imdecode(bytes_data, cv2.IMREAD_COLOR)
        return image
    except Exception:
        return None

# 리사이즈 + 대비 강화 함수
def resize_and_enhance(image_path, max_width=800):
    image_path = os.path.abspath(image_path)
    image = imread_unicode(image_path)
    if image is None:
        return image_path

    h, w = image.shape[:2]
    if w > max_width:
        scale = max_width / w
        image = cv2.resize(image, (int(w * scale), int(h * scale)))

    # 대비 강화 (히스토그램 평활화)
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    equalized = cv2.equalizeHist(gray)
    enhanced_image = cv2.cvtColor(equalized, cv2.COLOR_GRAY2BGR)

    temp_path = "resized_enhanced.jpg"
    cv2.imwrite(temp_path, enhanced_image)
    return temp_path

# 번호판 추출 함수
def extract_plate(img_path):
    if not os.path.exists(img_path):
        return "FILE_NOT_FOUND"
    try:
        reader = easyocr.Reader(['ko', 'en'], gpu=True, verbose=False)
        processed_path = resize_and_enhance(img_path)
        results = reader.readtext(processed_path)

        pattern = re.compile(r'\d{2,3}[가-힣]\d{4}')
        for _, text, conf in results:
            cleaned = re.sub(r'\s+', '', text)
            match = pattern.search(cleaned)
            if match:
                return match.group()

        return "NOT_FOUND"
    except Exception as e:
        return f"ERROR: {str(e)}"

# 실행 부분
if __name__ == '__main__':
    if len(sys.argv) < 2:
        sys.stdout.write("NO_INPUT")
    else:
        result = extract_plate(sys.argv[1])
        sys.stdout.write(result)















