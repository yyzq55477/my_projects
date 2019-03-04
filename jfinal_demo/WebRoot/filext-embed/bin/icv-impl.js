/**
 * ICV控件的脚本部分
 * 
 * @author lish
 */


function getIcvConfig() {
	var me = this;
	return {
		bizCode : me.bizCode,
		bizId : me.bizId,
		fuId : me.fuId,
		config : me.config,
		hostId : me.hostId,
		lockId : me.lockId
	}
}

function getIcv() {
	return this;
}

function submit(bizId, config) {
	config.bizId = bizId;
	filextApi.submit(config);
}
function updatePicture( config) {
	filextApi.updatePicture(config);
	initPage();
}
function updateFileUnitDataTpye(fileUnieId) {
	filextApi.updateFileUnitDataTpye(fileUnieId);
}
function requiredValidate(){
	return filextApi.validate();
}
function getMaterialDetail(config){
	return filextApi.getMaterialDetail(config);
}
function setHandOverList(config){
	return filextApi.setHandOverList(config);
}

function sentMaterial(uuid,value,code) {
	filextApi.sentMaterial(uuid,value,code);
	initPage();
}
function checkMaterial(uuids) {

	var res=filextApi.checkMaterial(uuids);
	return res;
}
function returnApplication(config) {
	return filextApi.returnApplication(config);
}
function getInitialState(jgCode) {
	return	filextApi.getInitialState(jgCode);
}
function createAndSaveFileUnit(bizId,conut) {
	filextApi.createAndSaveFileUnit(bizId,conut);
}
function removeArchive(config) {
	filextApi.removeArchive(config);
}

function copyArchive(config) {
	var res = filextApi.copyArchive(config);
	if (res.success && config.render) {
		filextApi.uuid = res.data;
		this.complete = null;
		initPage();
	} else {
		return res;
	}
}

function modifyEffective(config) {
	var res = filextApi.modifyEffective(config);
	if (res.success) {
		initPage();
	}
}

function modifyRequired(config) {
	var res = filextApi.modifyRequired(config);
	if (res.success) {
		initPage();
	}
}

function disableOperate() {
	disableOperate();
}

function addCategory(config) {
	if (typeof(config.uuid) == 'undefined') {
		config.uuid = filextApi.uuid;
		config.isAdd = true;
	} else {
		config.isAdd = false;
	}
	
	var res = filextApi.addCategory(config);
	if (res.success) {
		initPage();
		$('#listView .thumbItem[data-id='+res.data+']').click();
		return res.data;
	} else {
		return null;
	}
}

function moveToMe(config) {
	config.foid = currentId;
	var res = filextApi.moveToMe(config);
	if (res.success) {
		updateCateView();
		return true;
	} else {
		return false;
	}
}

function submitBatch(config) {
	var res = filextApi.submitBatch(config);
	if (res.success) {
		return true;
	} else {
		return false;
	}
}

function viewCount(config) {
	config.foid = currentId;
	var res = filextApi.viewCount(config);
	return res;
}
