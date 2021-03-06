package wisegrid.sample;

import wisegrid.data.HeaderData;
import wisegrid.data.MultilineData;
import wisegrid.data.SampleData;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 동적헤더 생성 예제
 * @author iCOMPIA CORP.
 */
public class ExampleDynamicTest extends HttpServlet {
	private static final long serialVersionUID = 1145939859139543255L;

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		GridData gdReq = null;
		GridData gdRes = null;

		// WiseGrid는 Ajax를 이용하여 통신을 하기 때문에 UTF-8방식으로 주고 받아야한다.
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html;charset=UTF-8");

		PrintWriter out = res.getWriter();

		try {
			// WiseGrid에서 올라온 전문을 받아온다.
			String rawData = req.getParameter("WISEGRID_DATA");
			// 전문을 GridData 자료구조형태로 파싱한다.
			gdReq = OperateGridData.parse(rawData);

			String mode = gdReq.getParam("MODE");
			if(mode.equals("COM"))
				gdRes = doQueryDinamic1();
			else if(mode.equals("TERM"))
				gdRes = doQueryDinamic2();
			else if(mode.equals("U_COM"))
				gdRes = ModifyDinamic1(gdReq);
			else if(mode.equals("U_TERM"))
				gdRes = ModifyDinamic2(gdReq);

		} catch (Exception e) {
			gdRes = new GridData();
			gdRes.setMessage("Error: " + e.getMessage());
			gdRes.setStatus("false");
			e.printStackTrace();
		} finally {
			try {
				// 생성된 데이터를 전문화시켜 Write한다.
				OperateGridData.write(gdRes, out);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public GridData doQueryDinamic1() throws Exception {

		GridData gdRes = new GridData();
		int rowCount = 0;

		try {
			/* 본 예제는 DB Connection은 하지 않고 별도의 샘플 데이터를 사용해 조회한다.	 */
			SampleData sd = new SampleData();
			rowCount = sd.getRowcount();

			// 데이터가 없을 경우
			if (rowCount == 0) {
				gdRes.addParam("MODE", "COM");
				gdRes.setMessage("조회결과가 없습니다.");
				gdRes.setStatus("true");
				return gdRes;
			}

			//  동적으로 헤더를 생성
			HeaderData headerData = new HeaderData();
			List headerList = headerData.getDynamicHeaderList();
			Iterator ite = headerList.iterator();
			int i=0;
			while(ite.hasNext()){
				HashMap header = (HashMap) ite.next();
				System.out.println("header: "+ header.get("key"));
				gdRes.addDynamicHeader((String)header.get("key"), (String)header.get("text"), (String)header.get("type"), (String)header.get("maxLength"), (String)header.get("width"), (String)header.get("edit"));

				if(header.get("type").equals(OperateGridData.t_combo)){
					gdRes.getHeader((String)header.get("key")).addSelectedHiddenValue("");
				}else if(header.get("type").equals(OperateGridData.t_checkbox)){
					gdRes.getHeader((String)header.get("key")).addValue("0", "");
				}else if(header.get("type").equals(OperateGridData.t_imagetext)){
					gdRes.getHeader((String)header.get("key")).addValue("imagetext", "" ,0);
				}else if(header.get("type").equals(OperateGridData.t_number)){
					gdRes.getHeader((String)header.get("key")).addValue("100", "");
				}else {
					gdRes.getHeader((String)header.get("key")).addValue("20070501", "");
				}
			}


			// 해당 컬럼에 콤보리스트를 추가
			String[] comboValues_1 = {"프로세서", "메모리", "메인보드", "그래픽카드" , "모니터" , "하드디스크" , "CDROM" , "키보드" , "마우스"};
			String[] comboHiddenValues_1 ={"CPU", "MEM", "MAB", "VID" , "MOR" , "HDD" , "CDR" , "KEY" , "MOU"};
			gdRes.getHeader("ITEM_FLAG").setComboValues(comboValues_1, comboHiddenValues_1);

			String[] comboValues_2 = {"EA"};
			String[] comboHiddenValues_2 ={"EA"};
			//gdRes.getHeader("UNIT").setComboValues(comboValues_2, comboHiddenValues_2);

			// 해당 컬럼에 이미지 리스트를 추가
			String[] imagelist = {"../../images/bt_search.gif"};
			gdRes.getHeader("ITEM_CODE").setImageURLs(imagelist);

			// Client API 를 서버 쪽에서 셋팅하기 위한 메소드
			// 첫번째 인자로 메소드 명을 주고 두번째 인자로 메소드에 들어갈 Param 값을 넣어준다.




			// 메세지 및 Param들을 셋팅한다.
			gdRes.setMessage("성공적으로 작업하였습니다");
			gdRes.setStatus("true");
			gdRes.addParam("MODE", "COM");
		} catch (Exception e) {
			throw e;
		}

		return gdRes;
	}

	public GridData doQueryDinamic2() throws Exception {

		GridData gdRes = new GridData();
		int rowCount = 0;

		try {
			//  동적으로 헤더를 생성
			gdRes.addDynamicHeader("SELECTED", "선택", OperateGridData.t_checkbox, "2", "40", "true");
			gdRes.addDynamicHeader("NAME", "용어", OperateGridData.t_imagetext, "100", "150", "true");
			gdRes.addDynamicHeader("SUMMATION", "요약", OperateGridData.t_text, "1000", "500", "true");
			gdRes.addDynamicHeader("DESCRIPTION", "본문", OperateGridData.t_text, "4000", "200", "true");

			// Client API 를 서버 쪽에서 셋팅하기 위한 메소드
			// 첫번째 인자로 메소드 명을 주고 두번째 인자로 메소드에 들어갈 파람 값을 넣어준다.
			String[] strParams1 = {"DESCRIPTION","true"};
			gdRes.addDynamicAPI(DinamicMethods.SetColHide, strParams1);

			String[] strParams2= {"NAME","0|0|255"};
			gdRes.addDynamicAPI(DinamicMethods.SetColCellFgColor, strParams2);

			String[] strParams3 = {"NAME", "true"};
			gdRes.addDynamicAPI(DinamicMethods.SetColCellMultiLine, strParams3);

			String[] strParams4 = {"SUMMATION", "true"};
			gdRes.addDynamicAPI(DinamicMethods.SetColCellMultiLine, strParams4);

			// 본 예제는 DB Connection은 하지 않고 별도의 샘플 데이터를 사용해 조회한다.
			MultilineData md = new MultilineData();
			rowCount = md.getRowcount();

			// 데이터가 없을 경우
			if (rowCount == 0) {
				gdRes.addParam("MODE", "TERM");
				gdRes.setMessage("조회결과가 없습니다.");
				gdRes.setStatus("true");
				return gdRes;
			}

			// 데이터셋팅
			for (int i = 0; i < rowCount; i++) {
				gdRes.getHeader("SELECTED").addValue("0", "");
				gdRes.getHeader("NAME").addValue(md.getValue(i, 0),	String.valueOf(i), -1);
				gdRes.getHeader("SUMMATION").addValue(md.getValue(i, 1), "");
				gdRes.getHeader("DESCRIPTION").addValue(md.getValue(i, 2), "");
			}

			// 메세지 및 Param들을 셋팅한다.
			gdRes.setMessage("성공적으로 작업하였습니다");
			gdRes.setStatus("true");
			gdRes.addParam("MODE", "TERM");

		} catch (Exception e) {
			throw e;
		}

		return gdRes;
	}

	private GridData ModifyDinamic1(GridData gdReq) throws Exception {

		GridData gdRes = new GridData();
		int rowCount = 0;

		// append한 StringBuffer를 update_data에 넘긴다.
		String updateData = "";

		try {
			// 화면에서 전달받은 "SEQ_NO"의 Count를 가져온다.
			rowCount = gdReq.getHeader("SEQ_NO").getRowCount();

			// 수정시 입력할 데이터를 정해진 형태로 만들어 놓는다.
			String inData[][] = new String[rowCount][];

			for (int i = 0; i < rowCount; i++) {
				String Data[] = {
						gdReq.getHeader("ITEM_FLAG").getComboHiddenValues()[gdReq.getHeader("ITEM_FLAG").getSelectedIndex(i)],
						gdReq.getHeader("VENDOR_NAME").getValue(i),
						gdReq.getHeader("ITEM_CODE").getValue(i),
						gdReq.getHeader("ITEM_NAME").getValue(i),
						gdReq.getHeader("SPECIFICATION").getValue(i),
						gdReq.getHeader("UNIT").getComboHiddenValues()[gdReq.getHeader("UNIT").getSelectedIndex(i)],
						gdReq.getHeader("PRICE").getValue(i),
						gdReq.getHeader("STOCK").getValue(i) };
				inData[i] = Data;
			}

			/*
			 * 생성된 자료구조를 DataBase에 넘겨 처리한다.
			 */

			// 이 예제는 동적헤더의 동작을 확인하기 위한 샘플이므로
			// 만들어진 데이터를 화면의 fieldset으로  보내 정상적으로 통신이 이루어졌는지 확인한다.
			updateData = getSendData(inData);

			/* 화면에 전달할  파라미터를 설정한다.
			 * 메세지를 셋팅한다.
			 * Status를 설정한다
			 */
			gdRes.addParam("MODE", "U_COM");
			gdRes.addParam("update_data", updateData);
			gdRes.setMessage("성공적으로 작업하였습니다.");
			gdRes.setStatus("true");

		} catch (Exception e) {
			throw e;
		}

		return gdRes;
	}


	private GridData ModifyDinamic2(GridData gdReq) throws Exception {
		GridData gdRes = new GridData();

		int rowCount = 0;

		// append한 StringBuffer를 update_data에 넘긴다.
		String updateData = "";

		try {
			// 화면에서 전달받은 "SEQ_NO"의 Count를 가져온다.
			rowCount = gdReq.getHeader("SELECTED").getRowCount();

			// 수정시 입력할 데이터를 정해진 형태로 만들어 놓는다.
			String inData[][] = new String[rowCount][];

			for (int i = 0; i < rowCount; i++) {
				String Data[] = {
						gdReq.getHeader("NAME").getValue(i),
						gdReq.getHeader("SUMMATION").getValue(i),
						gdReq.getHeader("DESCRIPTION").getValue(i)};
				inData[i] = Data;
			}

			/*
			 * 생성된 자료구조를 DataBase에 넘겨 처리한다.
			 */

			// 이 예제는 동적헤더의 동작을 확인하기 위한 샘플이므로
			// 만들어진 데이터를 화면의 fieldset으로  보내 정상적으로 통신이 이루어졌는지 확인한다.
			updateData = getSendData(inData);

			/* 화면에 전달할 파라미터를 설정한다.
			 * 메세지를 셋팅한다.
			 * Status를 설정한다
			 */
			gdRes.addParam("MODE", "U_TERM");
			gdRes.addParam("update_data", updateData);
			gdRes.setMessage("성공적으로 작업하였습니다.");
			gdRes.setStatus("true");

		} catch (Exception e) {
			throw e;
		}

		return gdRes;
	}

	private String getSendData(String[][] sendData) {

		StringBuffer sbData = new StringBuffer();

		for(int i = 0; i < sendData.length; i++) {
			String[] rowData = sendData[i];
			for(int j = 0; j < rowData.length; j++)
				sbData.append("[" + rowData[j] + "]");
			sbData.append("\n");
		}
		sbData.append(sendData.length + " 건의 데이터가 수정되었습니다.\n");

		return sbData.toString();
	}

	/**
	 * 동적헤더에서 사용가능한 Dinamic Method List
	 * @author iCOMPIA CORP.
	 */
	public static class DinamicMethods {
		public static String AddAreaMerge = "AddAreaMerge";
		public static String AddSummaryBar = "AddSummaryBar";
		public static String CollapseTreeAll = "CollapseTreeAll";
		public static String CollapseTreeNode = "CollapseTreeNode";
		public static String ExpandTreeAll = "ExpandTreeAll";
		public static String ExpandTreeNode = "ExpandTreeNode";
		public static String SetAreaMergeColor = "SetAreaMergeColor";
		public static String SetAreaMergeHAlign = "SetAreaMergeHAlign";
		public static String SetAreaMergeText = "SetAreaMergeText";
		public static String SetAreaMergeVAlign = "SetAreaMergeVAlign";
		public static String SetColCellActivation = "SetColCellActivation";
		public static String SetColCellAlign = "SetColCellAlign";
		public static String SetColCellBgColor = "SetColCellBgColor";
		public static String SetColCellCopyEnable = "SetColCellCopyEnable";
		public static String SetColCellDisplayEllipses = "SetColCellDisplayEllipses";
		public static String SetColCellExcelAsterisk = "SetColCellExcelAsterisk";
		public static String SetColCellFgColor = "SetColCellFgColor";
		public static String SetColCellFont = "SetColCellFont";
		public static String SetColCellFontBold = "SetColCellFontBold";
		public static String SetColCellFontCLine = "SetColCellFontCLine";
		public static String SetColCellFontItalic = "SetColCellFontItalic";
		public static String SetColCellFontName = "SetColCellFontName";
		public static String SetColCellFontSize = "SetColCellFontSize";
		public static String SetColCellFontULine = "SetColCellFontULine";
		public static String SetColCellMerge = "SetColCellMerge";
		public static String SetColCellMultiLine = "SetColCellMultiLine";
		public static String SetColCellRadio = "SetColCellRadio";
		public static String SetColCellSort = "SetColCellSort";
		public static String SetColCellSortEnable = "SetColCellSortEnable";
		public static String SetColEvent = "SetColEvent";
		public static String SetColFix = "SetColFix";
		public static String SetColHDAlign = "SetColHDAlign";
		public static String SetColHDBgColor = "SetColHDBgColor";
		public static String SetColHDCheckBoxVisible = "SetColHDCheckBoxVisible";
		public static String SetColHDFgColor = "SetColHDFgColor";
		public static String SetColHDText = "SetColHDText";
		public static String SetColHiddenValue = "SetColHiddenValue";
		public static String SetColHide = "SetColHide";
		public static String SetColIndex = "SetColIndex";
		public static String SetColLevel = "SetColLevel";
		public static String SetColWidth = "SetColWidth";
		public static String SetCRUDMode = "SetCRUDMode";
		public static String SetDateFormat = "SetDateFormat";
		public static String SetGroupHDColor = "SetGroupHDColor";
		public static String SetGroupHDFont = "SetGroupHDFont";
		public static String SetGroupHDText = "SetGroupHDText";
		public static String SetGroupMerge = "SetGroupMerge";
		public static String SetHDLevel = "SetHDLevel";
		public static String SetImageListSize = "SetImageListSize";
		public static String SetImagetextAlign = "SetImagetextAlign";
		public static String SetImagetextCursor = "SetImagetextCursor";
		public static String SetNumberFormat = "SetNumberFormat";
		public static String SetNumberNegative = "SetNumberNegative";
		public static String SetScrollTip = "SetScrollTip";
		public static String SetSortInfo = "SetSortInfo";
		public static String SetSummaryBarColor = "SetSummaryBarColor";
		public static String SetSummaryBarFont = "SetSummaryBarFont";
		public static String SetSummaryBarText = "SetSummaryBarText";
		public static String SetTreeMode = "SetTreeMode";
	}

}
