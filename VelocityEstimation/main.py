    ## Step 1: Detect Features
    # Three methods Tested :- Harris Corners, Shi - Tomasi Method , SURF
    #
    # harris corner method
    #dst = cv2.cornerHarris(gray,2,3,0.04)
    #
    # Shi - Tomasi Method
    #corners = cv2.goodFeaturesToTrack(gray,25,0.01,10)
    #
    # SURF
    #surf = cv2.xfeatures2d.SURF_create()
    #kp,des = surf.detectAndCompute(frame,None)
    #
    ## Step 2 : Calculate Optical Flow
    # Lucas Kanade 
    # cv2.calcOpticalFlowPyrLK(first_gray, gray, kp, None)





import numpy as np
import cv2
import urllib
# Parameters for lucas kanade optical flow
lk_params = dict( winSize  = (15,15),
                  maxLevel = 2,
                  criteria = (cv2.TERM_CRITERIA_EPS | cv2.TERM_CRITERIA_COUNT, 10, 0.03))
url='http://192.168.1.102:8081/shot.jpg?rnd=522179'
imgResp=urllib.urlopen(url)
imgNp=np.array(bytearray(imgResp.read()),dtype=np.uint8)
old_frame = cv2.imdecode(imgNp,-1)
old_gray = np.float32(cv2.cvtColor(old_frame,cv2.COLOR_BGR2GRAY))

old_gray = cv2.cvtColor(old_frame, cv2.COLOR_BGR2GRAY)

surf = cv2.xfeatures2d.SURF_create(4000)
kp,des = surf.detectAndCompute(old_frame,None)
print kp

pt2 = []
pt2_narray = np.empty((0,2), float)
for each_point in kp:
    pt2.append(each_point.pt)
    x = each_point.pt[0]
    y = each_point.pt[1]
    pt2_narray = np.vstack((pt2_narray, np.array([x, y])))

pt2_narray =  np.float32(pt2_narray).reshape(-1, 1, 2)
p0 = pt2_narray


# Create a mask image for drawing purposes
mask = np.zeros_like(old_frame)
while(1):
    imgResp=urllib.urlopen(url)
    imgNp=np.array(bytearray(imgResp.read()),dtype=np.uint8)
    frame = cv2.imdecode(imgNp,-1)
    gray = np.float32(cv2.cvtColor(old_frame,cv2.COLOR_BGR2GRAY))
    
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    # calculate optical flow
    print p0
    p1, st, err = cv2.calcOpticalFlowPyrLK(old_gray, gray, p0, None, **lk_params)
    # Select good points
    good_new = p1[st==1]
    good_old = p0[st==1]
    # draw the tracks
    

    for i,(new,old) in enumerate(zip(good_new,good_old)):
        a,b = new.ravel()
        c,d = old.ravel()
        mask = cv2.line(mask, (a,b),(c,d),(255,0,0), 2)
        frame = cv2.circle(frame,(a,b),5,(255,0,0),-1)
    
    img = cv2.add(frame,mask)

    cv2.imshow('frame',img)
    k = cv2.waitKey(30) & 0xff
    if k == 27:
        break
    # Now update the previous frame and previous points
    old_gray = gray.copy()
    p0 = good_new.reshape(-1,1,2)
cv2.destroyAllWindows()
cap.release()